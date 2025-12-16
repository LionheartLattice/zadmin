package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
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
    public ZFile upload(MultipartFile file, String usage) {
        // 1. 生成雪花算法 ID
        BigDecimal snowflakeId = (BigDecimal) snowflakePrimaryKeyGenerator.getPrimaryKey();

        // 2. 构造文件名 (格式: yyyyMM/雪花ID(去除yyyyMM).后缀)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String fileKey = buildFileKey(snowflakeId, suffix);

        // 3. 上传到 OSS
        OssPutRet putRet = ossService.upload(file, fileKey);

        // 4. 构建实体并保存到数据库 (主键直接存储 fileKey)
        ZFile zFile = new ZFile().setId(fileKey)
                                 .setUsage(usage)
                                 .setOriginalName(putRet.getOriginalName())
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
     * @param file  文件
     * @param usage 用途
     * @return 文件访问链接
     */
    @Transactional(rollbackFor = Exception.class)
    public String uploadReturnUrl(MultipartFile file, String usage) {
        ZFile zFile = upload(file, usage);
        return ossService.getPublicUrl(zFile.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String fileKey) {
        // 1. 删除数据库记录
        easyEntityQuery.deletable(ZFile.class)
                       .where(f -> f.id()
                                    .eq(fileKey))
                       .executeRows();

        // 2. 删除 OSS 文件
        ossService.delete(fileKey);
    }

    /**
     * 根据文件KEY获取访问链接
     *
     * @param fileKey 文件KEY (主键)
     * @return 文件访问URL
     */
    public String getUrlByKey(String fileKey) {
        return ossService.getPublicUrl(fileKey);
    }

    /**
     * 根据雪花ID和后缀构建文件KEY
     * 策略: 提取 ID 前6位(yyyyMM)作为文件夹,文件名为去除前6位后的ID
     * 格式: yyyyMM/雪花ID(去除yyyyMM).后缀
     */
    private String buildFileKey(BigDecimal snowflakeId, String extension) {
        String idStr = snowflakeId.toPlainString();
        // 雪花算法ID结构: yyyyMMddHHmmssSSS... (前6位为 yyyyMM)
        String monthFolder = idStr.substring(0, 6);
        String fileNamePart = idStr.substring(6); // 去除前6位的 yyyyMM

        if (extension == null || extension.isBlank()) {
            return monthFolder + "/" + fileNamePart;
        }
        return monthFolder + "/" + fileNamePart + "." + extension;
    }
}
