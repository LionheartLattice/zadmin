package io.github.lionheartlattice.configuration.easyquery;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.spring.SpringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

public class DataAccessUtils {

    public static JdbcTemplate getJdbcTemplate() {
        return SpringUtils.getBean("primaryJdbcTemplate", JdbcTemplate.class);
    }

    public static JdbcTemplate getDs2JdbcTemplate() {
        return SpringUtils.getBean("ds2JdbcTemplate", JdbcTemplate.class);
    }

    public static EasyEntityQuery getEntityQuery() {
        return SpringUtils.getBean("primaryEasyEntityQuery", EasyEntityQuery.class);
    }

    public static EasyEntityQuery getDs2EntityQuery() {
        return SpringUtils.getBean("ds2EasyEntityQuery", EasyEntityQuery.class);
    }

    public static StringRedisTemplate getStringRedisTemplate() {
        return SpringUtils.getBean(StringRedisTemplate.class);
    }
}
