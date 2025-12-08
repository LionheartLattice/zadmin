package io.github.lionheartlattice.configuration.SnowflakeProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 雪花算法配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {

    /**
     * 起始时间戳，默认 2024-01-01 00:00:00
     */
    private long epoch = 1704067200000L;

    /**
     * 机器ID，默认为1
     */
    private long workerId = 1L;
}
