package io.github.lionheartlattice.configuration.easyquery;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.spring.SpringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据访问工具类，提供对常用数据访问组件的静态访问
 *
 * @author lionheart
 */
public class DataAccessUtils {

    /**
     * 获取主数据源的JdbcTemplate
     *
     * @return JdbcTemplate实例
     */
    public static JdbcTemplate getJdbcTemplate() {
        return SpringUtils.getBean("primaryJdbcTemplate", JdbcTemplate.class);
    }

    /**
     * 获取第二个数据源的JdbcTemplate
     *
     * @return JdbcTemplate实例
     */
    public static JdbcTemplate getDs2JdbcTemplate() {
        return SpringUtils.getBean("ds2JdbcTemplate", JdbcTemplate.class);
    }

    /**
     * 获取主数据源的EasyEntityQuery
     *
     * @return EasyEntityQuery实例
     */
    public static EasyEntityQuery getEntityQuery() {
        return SpringUtils.getBean(EasyEntityQuery.class);
    }

    /**
     * 获取第二个数据源的EasyEntityQuery
     *
     * @return EasyEntityQuery实例
     */
    public static EasyEntityQuery getDs2EntityQuery() {
        return SpringUtils.getBean("ds2", EasyEntityQuery.class);
    }

    /**
     * 获取StringRedisTemplate
     *
     * @return StringRedisTemplate实例
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return SpringUtils.getBean(StringRedisTemplate.class);
    }
}
