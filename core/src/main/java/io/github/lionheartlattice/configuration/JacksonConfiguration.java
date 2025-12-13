package io.github.lionheartlattice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.lionheartlattice.BigDecimalToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Jackson 全局配置
 * 配置 BigDecimal 序列化为字符串，解决前端 JS 精度丢失问题
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public SimpleModule bigDecimalModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalToStringSerializer());
        return module;
    }

    @Bean
    public ObjectMapper objectMapper(SimpleModule bigDecimalModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(bigDecimalModule);
        return objectMapper;
    }
}
