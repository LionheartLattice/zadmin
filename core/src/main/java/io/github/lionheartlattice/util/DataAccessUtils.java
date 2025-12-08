package io.github.lionheartlattice.util;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 数据访问工具类
 */
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

    /**
     * 获取主数据源 TransactionTemplate
     */
    public static TransactionTemplate getTransactionTemplate() {
        return SpringUtils.getBean("primaryTransactionTemplate", TransactionTemplate.class);
    }

    /**
     * 获取第二数据源 TransactionTemplate
     */
    public static TransactionTemplate getDs2TransactionTemplate() {
        return SpringUtils.getBean("ds2TransactionTemplate", TransactionTemplate.class);
    }
}
