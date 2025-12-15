package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.parent.ZFile;
import io.github.lionheartlattice.entity.user_center.vo.OssPutRet;
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
        String fileKey = id.toPlainString() + (suffix.isEmpty() ? "" : "." + suffix);

        // 3. 上传到 OSS
        OssPutRet putRet = ossService.upload(file, fileKey);

        // 4. 构建实体并保存到数据库
        ZFile zFile = new ZFile();
        zFile.setId(id); // 手动设置ID
        zFile.setOriginalName(putRet.getOriginalName());
        zFile.setFileKey(putRet.getFileKey());
        zFile.setExtension(putRet.getExtension());
        zFile.setFileSize(putRet.getFileSize());
        zFile.setContentType(putRet.getContentType());

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
        return ossService.getPublicUrl(zFile.getFileKey());
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

        // 3. 删除 OSS 文件
        ossService.delete(zFile.getFileKey());
    }
}
