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
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.IOException;
import java.io.InputStream;

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
     *
     * @param file 前端上传的文件
     * @return 完整的 HTTP 访问链接
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        // 生成唯一文件名: 2024/01/uuid.png (建议按日期分目录，避免单目录下文件过多)
        String key = IdUtil.fastSimpleUUID() + "." + suffix;

        try (InputStream inputStream = file.getInputStream()) {
            // 1. 构建上传请求
            UploadRequest uploadRequest = UploadRequest.builder()
                                                       .putObjectRequest(b -> b.bucket(s3Properties.getBucketName())
                                                                               .key(key)
                                                                               .contentType(file.getContentType()))
                                                       .requestBody(
                                                               software.amazon.awssdk.core.async.AsyncRequestBody.fromInputStream(
                                                                       inputStream, file.getSize(),
                                                                       java.util.concurrent.Executors.newSingleThreadExecutor()))
                                                       // 注意：fromInputStream 需要指定 executor，或者使用 fromFile (如果先存临时文件)
                                                       // 这里为了简化直接用流，但在极高并发下建议先转临时文件再用 fromFile
                                                       .build();

            // 2. 开始异步上传
            Upload upload = s3TransferManager.upload(uploadRequest);

            // 3. 等待上传完成 (阻塞等待结果，因为 Controller 需要返回 URL)
            CompletedUpload completedUpload = upload.completionFuture()
                                                    .join();
            log.info("文件上传成功 ETag: {}", completedUpload.response()
                                                             .eTag());

            // 4. 返回固定公开链接
            return getPublicUrl(key);

        } catch (IOException e) {
            log.error("文件流读取失败", e);
            throw new RuntimeException("文件上传失败");
        } catch (Exception e) {
            log.error("S3上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
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
