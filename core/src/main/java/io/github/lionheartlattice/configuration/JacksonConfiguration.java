package io.github.lionheartlattice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Jackson 全局配置
 * 用于解决前端 JavaScript 处理大数字(如雪花ID)时的精度丢失问题
 */
@Configuration
public class JacksonConfiguration {

    /**
     * 配置 JacksonJsonHttpMessageConverter
     * <p>
     * 针对 Spring Boot 4.0.0 / Spring Framework 7.0 / Jackson 3.x 的变更：
     * 1. 包名由 com.fasterxml.jackson 变更为 tools.jackson
     * 2. ObjectMapper 变更为 JsonMapper
     * 3. JsonSerializer 变更为 ValueSerializer
     * 4. MappingJackson2HttpMessageConverter 变更为 JacksonJsonHttpMessageConverter
     *
     * @param jsonMapper Spring Boot 自动配置的 JsonMapper (包含 application.yml 配置)
     */
    @Bean
    public JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter(JsonMapper jsonMapper) {
        SimpleModule module = new SimpleModule();

        // 1. BigDecimal -> String (避免科学计数法，使用 toPlainString)
        module.addSerializer(BigDecimal.class, new BigDecimalToStringSerializer());

        // 2. BigInteger -> String (处理超大整数)
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);

        // 3. Long -> String (解决 JS Long 精度丢失)
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // Jackson 3: 使用 rebuild() 基于现有配置创建新的 Mapper，并添加自定义模块
        // 这样既保留了 application.yml 中的配置(如时区)，又应用了我们的序列化规则
        JsonMapper newMapper = jsonMapper.rebuild()
                                         .addModule(module)
                                         .build();

        return new JacksonJsonHttpMessageConverter(newMapper);
    }

    /**
     * 自定义 BigDecimal 序列化器
     * 将 BigDecimal 转换为纯字符串，避免科学计数法和精度丢失
     * 注意：Jackson 3.x 中 JsonSerializer 已重命名为 ValueSerializer
     */
    public static class BigDecimalToStringSerializer extends ValueSerializer<BigDecimal> {
        /**
         * Jackson 3.x API 变更：
         * serialize 方法的第三个参数由 SerializerProvider 变更为 SerializationContext
         */
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext ctxt) {
            if (value != null) {
                // 使用 toPlainString() 确保输出为普通数字字符串 (例如 "2025..." 而不是 "2.025E+27")
                gen.writeString(value.toPlainString());
            } else {
                gen.writeNull();
            }
        }
    }
}
