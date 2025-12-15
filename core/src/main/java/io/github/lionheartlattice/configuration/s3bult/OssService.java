package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import io.github.lionheartlattice.entity.user_center.vo.OssPutRet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 对象存储底层服务 (S3 Wrapper)
 * 专注于文件的上传、删除和URL生成，不处理业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssService {

    private final S3TransferManager s3TransferManager;
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    /**
     * 上传文件 (需指定 Key)
     *
     * @param file 前端上传的文件
     * @param key  对象存储 Key (通常由业务层生成，如 ID.后缀)
     * @return 上传结果
     */
    public OssPutRet upload(MultipartFile file, String key) {
        File tempFile = null;
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = FileUtil.getSuffix(originalFilename);
            String contentType = file.getContentType();
            long size = file.getSize();

            // 1. 创建临时文件
            Path tempPath = Files.createTempFile("oss_upload_", "." + suffix);
            tempFile = tempPath.toFile();

            // 2. 将 MultipartFile 写入临时文件
            file.transferTo(tempFile);

            // 3. 构建文件上传请求
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                                                                   .putObjectRequest(
                                                                           b -> b.bucket(s3Properties.getBucketName())
                                                                                 .key(key)
                                                                                 .contentType(contentType))
                                                                   .source(tempFile)
                                                                   .build();

            // 4. 执行异步上传
            FileUpload upload = s3TransferManager.uploadFile(uploadFileRequest);

            // 5. 等待上传完成
            CompletedFileUpload completedUpload = upload.completionFuture()
                                                        .join();
            log.info("S3上传成功 ETag: {}, Key: {}", completedUpload.response()
                                                                    .eTag(), key);

            // 6. 返回详细结果
            return new OssPutRet().setOriginalName(originalFilename)
                                  .setFileKey(key)
                                  .setExtension(suffix)
                                  .setFileSize(size)
                                  .setContentType(contentType)
                                  .setUrl(getPublicUrl(key));

        } catch (IOException e) {
            log.error("文件转存临时文件失败", e);
            throw new RuntimeException("文件上传失败: 临时文件处理错误");
        } catch (Exception e) {
            log.error("S3上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            // 7. 清理临时文件
            if (tempFile != null) {
                FileUtil.del(tempFile);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param key 文件路径 (可以是完整URL，也可以是Key)
     */
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        String endpoint = getFormattedEndpoint();
        String prefix = endpoint + "/" + s3Properties.getBucketName() + "/";
        if (key.startsWith(prefix)) {
            key = key.substring(prefix.length());
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                                                         .bucket(s3Properties.getBucketName())
                                                         .key(key)
                                                         .build();
        s3Client.deleteObject(request);
    }

    /**
     * 获取文件的公开访问 URL
     */
    public String getPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        return getFormattedEndpoint() + "/" + s3Properties.getBucketName() + "/" + key;
    }

    private String getFormattedEndpoint() {
        String endpoint = s3Properties.getEndpoint();
        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
            endpoint = "http://" + endpoint;
        }
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint;
    }
}
