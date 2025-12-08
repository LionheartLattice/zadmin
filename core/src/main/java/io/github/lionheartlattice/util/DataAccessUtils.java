package io.github.lionheartlattice.util;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import static io.github.lionheartlattice.configuration.easyquery.MultiDataSourceConfiguration.*;

/**
 * 数据访问工具类
 *
 * @author lionheart
 */
public class DataAccessUtils {

    /**
     * 获取指定数据源的组件
     */
    private static <T> T getDataSourceBean(String dsName, String suffix, Class<T> clazz) {
        return SpringUtils.getBean(dsName + suffix, clazz);
    }

    // JdbcTemplate
    public static JdbcTemplate getJdbcTemplate() {
        return getDataSourceBean(PRIMARY, JDBC_TEMPLATE, JdbcTemplate.class);
    }

    public static JdbcTemplate getDs2JdbcTemplate() {
        return getDataSourceBean(DS2, JDBC_TEMPLATE, JdbcTemplate.class);
    }

    // EasyEntityQuery
    public static EasyEntityQuery getEntityQuery() {
        return getDataSourceBean(PRIMARY, EASY_ENTITY_QUERY, EasyEntityQuery.class);
    }

    public static EasyEntityQuery getDs2EntityQuery() {
        return getDataSourceBean(DS2, EASY_ENTITY_QUERY, EasyEntityQuery.class);
    }

    // TransactionTemplate
    public static TransactionTemplate getTransactionTemplate() {
        return getDataSourceBean(PRIMARY, TRANSACTION_TEMPLATE, TransactionTemplate.class);
    }

    public static TransactionTemplate getDs2TransactionTemplate() {
        return getDataSourceBean(DS2, TRANSACTION_TEMPLATE, TransactionTemplate.class);
    }

    // Redis
    public static StringRedisTemplate getStringRedisTemplate() {
        return SpringUtils.getBean(StringRedisTemplate.class);
    }

    /**
     * 根据数据源名称动态获取JdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate(String dsName) {
        return getDataSourceBean(dsName, JDBC_TEMPLATE, JdbcTemplate.class);
    }

    /**
     * 根据数据源名称动态获取EasyEntityQuery
     */
    public static EasyEntityQuery getEntityQuery(String dsName) {
        return getDataSourceBean(dsName, EASY_ENTITY_QUERY, EasyEntityQuery.class);
    }
}
