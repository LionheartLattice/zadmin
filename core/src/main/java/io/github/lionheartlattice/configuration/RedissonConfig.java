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
 * Redisson 手动配置（不依赖 redisson-spring-boot-starter）
 * <p>
 * 说明：
 * 1) 直接复用 application.yml 的 spring.data.redis 配置
 * 2) 使用自定义 Jackson3Codec 适配 Jackson 3.x (JsonMapper)
 * 3) 直接注入 Spring 管理的 JsonMapper，避免手动构建时的模块缺失问题
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.data.redis.host:localhost}") String host, @Value("${spring.data.redis.port:6379}") int port, @Value("${spring.data.redis.database:0}") int database, @Value("${spring.data.redis.password:}") String password, @Value("${spring.data.redis.timeout:5s}") Duration timeout, JsonMapper jsonMapper) { // 注入 Spring 上下文中的 JsonMapper
        Config config = new Config();

        // 使用注入的 JsonMapper (已包含 Spring Boot 的默认配置和模块)
        // 解决了手动 Builder 缺少 findAndRegisterModules 方法的问题
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

        log.info("RedissonClient initialized (manual). redis://{}:{}, db={}", host, port, database);
        return Redisson.create(config);
    }

    /**
     * 自定义 Redisson Codec 以支持 Jackson 3 (JsonMapper)
     * Redisson 原生 JsonJacksonCodec 仅支持 Jackson 2 (ObjectMapper)
     */
    public static class Jackson3Codec extends BaseCodec {
        private final JsonMapper mapper;
        private final Encoder encoder;
        private final Decoder<Object> decoder;

        public Jackson3Codec(JsonMapper mapper) {
            this.mapper = mapper;

            // 将初始化逻辑移入构造函数，确保 this.mapper 已赋值，解决"变量可能尚未初始化"的问题
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
