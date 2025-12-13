package io.github.lionheartlattice.configuration.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

/**
 * Jackson 全局配置
 * 配置 BigDecimal 序列化为字符串,解决前端 JS 精度丢失问题
 */
@Configuration
public class JacksonConfiguration {

    /**
     * 自定义 Jackson 序列化模块
     */
    @Bean
    public SimpleModule bigDecimalModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalToStringSerializer());
        return module;
    }

    /**
     * 自定义 ObjectMapper
     * 直接创建并注册模块,不依赖已弃用的 Jackson2ObjectMapperBuilder
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(SimpleModule bigDecimalModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(bigDecimalModule);

        // 如果需要其他配置,可以在这里添加
        // objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }
}
