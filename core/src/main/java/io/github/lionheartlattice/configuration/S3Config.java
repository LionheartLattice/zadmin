package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.configuration.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * S3 客户端配置
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                                          .region(Region.of(s3Properties.getRegion()))
                                          .credentialsProvider(StaticCredentialsProvider.create(
                                                  AwsBasicCredentials.create(s3Properties.getAccessKey(),
                                                          s3Properties.getSecretKey())));

        // 如果配置了 endpoint，则覆盖默认的 AWS endpoint (用于 MinIO 或其他兼容服务)
        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint()
                                                               .isBlank()) {
            builder.endpointOverride(URI.create(s3Properties.getEndpoint()));
        }

        // 配置路径样式访问 (Path Style Access)，MinIO 必须开启
        if (s3Properties.isPathStyleAccess()) {
            builder.serviceConfiguration(S3Configuration.builder()
                                                        .pathStyleAccessEnabled(true)
                                                        .build());
        }

        return builder.build();
    }
}
