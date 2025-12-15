package io.github.lionheartlattice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * S3 对象存储配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class S3Properties {
    /**
     * 服务端点 (例如 MinIO: http://localhost:9000, AWS: https://s3.us-east-1.amazonaws.com)
     */
    private String endpoint;

    /**
     * 区域 (例如 us-east-1, cn-north-1)
     */
    private String region = "us-east-1";

    /**
     * 访问密钥 ID
     */
    private String accessKey;

    /**
     * 访问密钥 Secret
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 是否启用路径样式访问 (MinIO 等兼容服务通常需要开启)
     */
    private boolean pathStyleAccess = true;
}
