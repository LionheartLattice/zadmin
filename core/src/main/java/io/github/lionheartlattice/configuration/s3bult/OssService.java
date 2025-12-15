package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
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
 * 对象存储服务 (针对公开存储桶优化)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssService {

    private final S3TransferManager s3TransferManager;
    private final S3Client s3Client; // 用于删除等简单操作
    private final S3Properties s3Properties;

    /**
     * 上传文件并返回公开访问链接
     * <p>
     * 优化策略：
     * 1. 将 MultipartFile 转存为本地临时文件 (避免内存缓冲)
     * 2. 使用 S3TransferManager.uploadFile (支持零拷贝、自动分片、多线程并发)
     * 3. finally 块中强制删除临时文件
     *
     * @param file 前端上传的文件
     * @return 完整的 HTTP 访问链接
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        // 生成唯一文件名: uuid.png
        String key = IdUtil.fastSimpleUUID() + "." + suffix;

        File tempFile = null;
        try {
            // 1. 创建临时文件
            // 使用 java.nio.file.Files 创建临时文件，前缀 oss_upload_
            Path tempPath = Files.createTempFile("oss_upload_", "." + suffix);
            tempFile = tempPath.toFile();

            // 2. 将 MultipartFile 写入临时文件
            // 这一步 SpringMVC 会高效处理，如果是大文件会直接在磁盘间移动
            file.transferTo(tempFile);

            // 3. 构建文件上传请求 (UploadFileRequest 优于 UploadRequest+fromInputStream)
            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                                                                   .putObjectRequest(
                                                                           b -> b.bucket(s3Properties.getBucketName())
                                                                                 .key(key)
                                                                                 .contentType(file.getContentType()))
                                                                   .source(tempFile) // 直接指定文件源，SDK会自动处理分片和并发
                                                                   .build();

            // 4. 执行异步上传
            // uploadFile 返回 FileUpload 对象，它是 Upload 的子类，专门用于文件
            FileUpload upload = s3TransferManager.uploadFile(uploadFileRequest);

            // 5. 等待上传完成 (阻塞当前线程等待结果)
            CompletedFileUpload completedUpload = upload.completionFuture()
                                                        .join();
            log.info("文件上传成功 ETag: {}, Key: {}", completedUpload.response()
                                                                      .eTag(), key);

            // 6. 返回固定公开链接
            return getPublicUrl(key);

        } catch (IOException e) {
            log.error("文件转存临时文件失败", e);
            throw new RuntimeException("文件上传失败: 临时文件处理错误");
        } catch (Exception e) {
            log.error("S3上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            // 7. 清理临时文件
            if (tempFile != null) {
                // 使用 Hutool 删除文件，如果删除失败会尝试多次或忽略
                boolean deleted = FileUtil.del(tempFile);
                if (!deleted) {
                    log.warn("临时文件删除失败，请检查权限或磁盘占用: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param key 文件路径 (可以是完整URL，也可以是Key)
     */
    public void delete(String key) {
        // 如果传入的是完整URL，尝试提取Key
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
     * 格式: http://endpoint/bucket/key
     */
    public String getPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        // 如果已经是完整链接则直接返回
        if (key.startsWith("http")) {
            return key;
        }

        return getFormattedEndpoint() + "/" + s3Properties.getBucketName() + "/" + key;
    }

    /**
     * 格式化 Endpoint (确保无尾部斜杠，且包含协议头)
     */
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
