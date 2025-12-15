package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.proxy.core.draft.Draft1;
import com.easy.query.core.proxy.sql.Select;
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
    public ZFile upload(MultipartFile file, String usage) {
        // 1. 预先生成雪花算法 ID
        BigDecimal id = (BigDecimal) snowflakePrimaryKeyGenerator.getPrimaryKey();

        // 2. 构造文件名 (ID.后缀)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);

        // 动态构建 OSS Key (包含按月分文件夹逻辑)
        String fileKey = getFileKey(id, suffix);

        // 3. 上传到 OSS
        OssPutRet putRet = ossService.upload(file, fileKey);

        // 4. 构建实体并保存到数据库 (不存储 fileKey)
        ZFile zFile = new ZFile().setId(id) // 手动设置ID
                                 .setUsage(usage)
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
    public String uploadReturnUrl(MultipartFile file, String usage) {
        ZFile zFile = upload(file, usage);
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
     * 获取随机图片文件的访问链接
     *
     * @return 图片URL
     */
    public String getRandomImageUrl() {
        long count = easyEntityQuery.queryable(ZFile.class)
                .where(f -> f.contentType().likeMatchLeft("image/"))
                .count();

        if (count == 0) {
            return null;
        }

        long offset = (long) (Math.random() * count);

        ZFile zFile = easyEntityQuery.queryable(ZFile.class)
                .where(f -> f.contentType().likeMatchLeft("image/"))
                .limit(offset, 1)
                .firstOrNull();

        if (zFile == null) {
            return null;
        }

        String fileKey = getFileKey(zFile.getId(), zFile.getExtension());
        return ossService.getPublicUrl(fileKey);
    }

    /**
     * 根据 ID 和后缀拼接 OSS Key
     * 策略: 提取 ID 前6位(yyyyMM)作为文件夹，实现按月分片存储
     * 格式: yyyyMM/ID.后缀
     */
    private String getFileKey(BigDecimal id, String extension) {
        String idStr = id.toPlainString();
        // 雪花算法ID结构: yyyyMMddHHmmssSSS... (前17位为时间戳)
        // 截取前6位 (yyyyMM) 作为目录，例如: 202505/2025052914302512300001000001.png
        String monthFolder = idStr.substring(0, 6);

        if (StrUtil.isBlank(extension)) {
            return monthFolder + "/" + idStr;
        }
        return monthFolder + "/" + idStr + "." + extension;
    }

    /**
     * 根据ID获取文件访问链接
     * 需查询数据库获取文件后缀名，确保Key正确
     *
     * @param id 文件ID
     * @return 文件访问URL
     */
    public String getUrlById(BigDecimal id) {
        // 1. 查询数据库获取文件信息(主要是后缀名)
        Draft1<String> draft1 = easyEntityQuery.queryable(ZFile.class)
                                               .whereById(id)
                                               .select(z -> Select.DRAFT.of(z.extension()))
                                               .singleNotNull();
        // 2. 动态构建 OSS Key
        String fileKey = getFileKey(id, draft1.getValue1());

        // 3. 生成访问链接
        return ossService.getPublicUrl(fileKey);
    }

    public String getUrlByIdAndExtension(BigDecimal id, String extension) {
        String fileKey = getFileKey(id, extension);
        return ossService.getPublicUrl(fileKey);
    }
}
