package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.parent.OssPutRet;
import io.github.lionheartlattice.entity.parent.ZFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * 文件业务服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ZFileService {

    private final OssService ossService;
    private final EasyEntityQuery easyEntityQuery;
    private final SnowflakePrimaryKeyGenerator snowflakePrimaryKeyGenerator;

    @Transactional(rollbackFor = Exception.class)
    public ZFile upload(MultipartFile file) {
        // 1. 预先生成雪花算法 ID
        BigDecimal id = (BigDecimal) snowflakePrimaryKeyGenerator.getPrimaryKey();

        // 2. 构造文件名 (ID.后缀)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        // 动态构建 OSS Key
        String fileKey = getFileKey(id, suffix);

        // 3. 上传到 OSS
        OssPutRet putRet = ossService.upload(file, fileKey);

        // 4. 构建实体并保存到数据库 (不存储 fileKey)
        ZFile zFile = new ZFile().setId(id) // 手动设置ID
                                 .setOriginalName(putRet.getOriginalName())
                                 .setExtension(putRet.getExtension())
                                 .setFileSize(putRet.getFileSize())
                                 .setContentType(putRet.getContentType());

        // 插入数据库
        easyEntityQuery.insertable(zFile)
                       .executeRows();

        return zFile;
    }

    /**
     * 上传文件并返回访问链接
     *
     * @param file 文件
     * @return 文件访问链接
     */
    @Transactional(rollbackFor = Exception.class)
    public String uploadReturnUrl(MultipartFile file) {
        ZFile zFile = upload(file);
        // 动态还原 Key 以获取 URL
        String fileKey = getFileKey(zFile.getId(), zFile.getExtension());
        return ossService.getPublicUrl(fileKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(BigDecimal id) {
        // 1. 查询文件信息
        ZFile zFile = easyEntityQuery.queryable(ZFile.class)
                                     .where(f -> f.id()
                                                  .eq(id))
                                     .firstOrNull();

        if (zFile == null) {
            return;
        }

        // 2. 删除数据库记录
        easyEntityQuery.deletable(ZFile.class)
                       .where(f -> f.id()
                                    .eq(id))
                       .executeRows();

        // 3. 删除 OSS 文件 (动态还原 Key)
        String fileKey = getFileKey(zFile.getId(), zFile.getExtension());
        ossService.delete(fileKey);
    }

    /**
     * 根据 ID 和后缀拼接 OSS Key
     * 格式: ID.后缀 (如果后缀为空则仅 ID)
     */
    private String getFileKey(BigDecimal id, String extension) {
        if (StrUtil.isBlank(extension)) {
            return id.toPlainString();
        }
        return id.toPlainString() + "." + extension;
    }
}
