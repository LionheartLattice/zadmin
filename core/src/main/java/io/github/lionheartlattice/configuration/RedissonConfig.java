package io.github.lionheartlattice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redisson 手动配置（不依赖 redisson-spring-boot-starter）
 * <p>
 * 说明：
 * 1) 直接复用 application.yml 的 spring.data.redis 配置
 * 2) 使用 JsonJacksonCodec 并显式注册 JavaTimeModule 以支持 LocalDate 等 Java 8 时间类型
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.data.redis.host:localhost}") String host, @Value("${spring.data.redis.port:6379}") int port, @Value("${spring.data.redis.database:0}") int database, @Value("${spring.data.redis.password:}") String password, @Value("${spring.data.redis.timeout:5s}") Duration timeout) {
        Config config = new Config();

        // 创建 ObjectMapper 并注册 JavaTimeModule（支持 LocalDate/LocalDateTime 等）
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 使用配置好的 ObjectMapper 创建 JsonJacksonCodec
        JsonJacksonCodec codec = new JsonJacksonCodec(objectMapper);
        config.setCodec(codec);

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
}
