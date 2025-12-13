package io.github.lionheartlattice.configuration;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;

/**
 * Redisson 手动配置
 * <p>
 * 1. 适配 Jackson 3.x (tools.jackson.*)
 * 2. 注入 Spring 管理的 JsonMapper，复用全局配置（如时间模块、序列化策略）
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.data.redis.host:localhost}") String host,
                                         @Value("${spring.data.redis.port:6379}") int port,
                                         @Value("${spring.data.redis.database:0}") int database,
                                         @Value("${spring.data.redis.password:}") String password,
                                         @Value("${spring.data.redis.timeout:5s}") Duration timeout,
                                         JsonMapper jsonMapper) { // 核心修改：直接注入 Spring 容器中的 JsonMapper
        Config config = new Config();

        // 使用注入的 JsonMapper 初始化自定义 Codec
        // 这样无需手动处理 findAndRegisterModules，也能保证与 Spring MVC 的序列化行为一致
        config.setCodec(new Jackson3Codec(jsonMapper));

        SingleServerConfig single = config.useSingleServer()
                                          .setAddress("redis://" + host + ":" + port)
                                          .setDatabase(database);

        if (StringUtils.hasText(password)) {
            single.setPassword(password);
        }

        int timeoutMs = Math.toIntExact(timeout.toMillis());
        single.setTimeout(timeoutMs);
        single.setConnectTimeout(timeoutMs);

        log.info("RedissonClient initialized. redis://{}:{}, db={}", host, port, database);
        return Redisson.create(config);
    }

    /**
     * 自定义 Redisson Codec 以支持 Jackson 3 (JsonMapper)
     * 修复了变量初始化顺序问题
     */
    public static class Jackson3Codec extends BaseCodec {
        private final JsonMapper mapper;
        private final Encoder encoder;
        private final Decoder<Object> decoder;

        public Jackson3Codec(JsonMapper mapper) {
            this.mapper = mapper;

            // 将 Encoder/Decoder 的初始化移入构造函数
            // 确保此时 this.mapper 已经被赋值，避免 NullPointerException 或未初始化错误
            this.encoder = in -> {
                ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
                try {
                    ByteBufOutputStream os = new ByteBufOutputStream(out);
                    this.mapper.writeValue((OutputStream) os, in);
                    return out;
                } catch (Exception e) {
                    out.release();
                    throw new IOException(e);
                }
            };

            this.decoder = (buf, state) -> {
                try {
                    return this.mapper.readValue((InputStream) new ByteBufInputStream(buf), Object.class);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            };
        }

        @Override
        public Decoder<Object> getValueDecoder() {
            return decoder;
        }

        @Override
        public Encoder getValueEncoder() {
            return encoder;
        }

        @Override
        public Decoder<Object> getMapValueDecoder() {
            return decoder;
        }

        @Override
        public Encoder getMapValueEncoder() {
            return encoder;
        }

        @Override
        public Decoder<Object> getMapKeyDecoder() {
            return decoder;
        }

        @Override
        public Encoder getMapKeyEncoder() {
            return encoder;
        }
    }
}
