package io.github.lionheartlattice.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.MapperFeature;

/**
 * Jackson 3 配置：使 JSON 输出按类中字段声明顺序
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                // 禁用字母序排序，保持字段声明顺序
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                // 构造器参数优先（与声明顺序一致）
                .enable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST);
    }
}
