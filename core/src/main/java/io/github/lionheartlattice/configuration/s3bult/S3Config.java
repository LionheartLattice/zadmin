package io.github.lionheartlattice.configuration.s3bult;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;

/**
 * S3 客户端配置
 * 包含同步客户端(S3Client)和异步传输管理器(S3TransferManager)
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    /**
     * 辅助方法：处理 Endpoint 格式
     */
    private URI getEndpointUri() {
        String endpoint = s3Properties.getEndpoint();
        if (endpoint == null || endpoint.isBlank()) {
            return null;
        }
        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
            endpoint = "http://" + endpoint;
        }
        return URI.create(endpoint);
    }

    /**
     * 同步客户端 (用于简单的元数据操作，如删除、列举)
     */
    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                                          .region(Region.of(s3Properties.getRegion()))
                                          .credentialsProvider(StaticCredentialsProvider.create(
                                                  AwsBasicCredentials.create(s3Properties.getAccessKey(),
                                                          s3Properties.getSecretKey())));

        URI endpoint = getEndpointUri();
        if (endpoint != null) {
            builder.endpointOverride(endpoint);
        }

        if (s3Properties.isPathStyleAccess()) {
            builder.serviceConfiguration(S3Configuration.builder()
                                                        .pathStyleAccessEnabled(true)
                                                        .build());
        }

        return builder.build();
    }

    /**
     * 异步客户端 (S3TransferManager 的基础)
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                                                    .region(Region.of(s3Properties.getRegion()))
                                                    .credentialsProvider(StaticCredentialsProvider.create(
                                                            AwsBasicCredentials.create(s3Properties.getAccessKey(),
                                                                    s3Properties.getSecretKey())));

        URI endpoint = getEndpointUri();
        if (endpoint != null) {
            builder.endpointOverride(endpoint);
        }

        if (s3Properties.isPathStyleAccess()) {
            builder.serviceConfiguration(S3Configuration.builder()
                                                        .pathStyleAccessEnabled(true)
                                                        .build());
        }

        return builder.build();
    }

    /**
     * 传输管理器 (官方推荐的高性能文件传输工具)
     * 自动处理分片上传、断点续传和异步并发
     */
    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager.builder()
                                .s3Client(s3AsyncClient)
                                .build();
    }
}
