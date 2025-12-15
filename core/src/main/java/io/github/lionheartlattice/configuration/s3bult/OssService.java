package io.github.lionheartlattice.configuration.s3bult;

import cn.hutool.core.io.FileUtil;
import io.github.lionheartlattice.entity.parent.OssPutRet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * 对象存储底层服务 (S3 Wrapper)
 * 专注于文件的上传、删除和URL生成，不处理业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssService {

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
        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = FileUtil.getSuffix(originalFilename);
            String contentType = file.getContentType();
            long size = file.getSize();

            // 1. 构建 PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(s3Properties.getBucketName())
                                                                .key(key)
                                                                .contentType(contentType)
                                                                .build();

            // 2. 使用 ContentProvider 方式上传
            // 解决 "Content input stream does not support mark/reset" 问题
            // 允许 SDK 在签名或重试时重新获取流，而不是依赖流的 reset 功能
            RequestBody requestBody = RequestBody.fromContentProvider(() -> {
                try {
                    return file.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException("无法获取文件流", e);
                }
            }, size, contentType);

            s3Client.putObject(putObjectRequest, requestBody);

            log.info("S3上传成功 Key: {}", key);

            // 3. 返回详细结果
            return new OssPutRet().setOriginalName(originalFilename)
                                  .setExtension(suffix)
                                  .setFileSize(size)
                                  .setContentType(contentType)
                                  .setUrl(getPublicUrl(key));

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
