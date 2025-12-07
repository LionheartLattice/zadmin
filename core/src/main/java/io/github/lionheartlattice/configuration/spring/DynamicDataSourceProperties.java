package io.github.lionheartlattice.configuration.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.datasource")
@Component
public class DynamicDataSourceProperties {
    private Map<String, DataSourceProperties> dynamic = new LinkedHashMap<>();
}
