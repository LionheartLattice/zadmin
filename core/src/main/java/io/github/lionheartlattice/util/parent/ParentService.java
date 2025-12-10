package io.github.lionheartlattice.util.parent;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.util.DataAccessUtils;
import io.github.lionheartlattice.util.NullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 父级工具类，提供实体类操作的通用方法
 *
 * @param <P> 实体类类型
 * @author lionheart
 * @since 1.0
 */
public abstract class ParentService<P> extends NullUtil {

    /**
     *
     * 日志实例，使用实际子类的 Class
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 主数据源 JdbcTemplate
     */
    protected final JdbcTemplate jdbcTemplate = DataAccessUtils.getJdbcTemplate();

    /**
     * 第二数据源 JdbcTemplate
     */
    protected final JdbcTemplate ds2JdbcTemplate = DataAccessUtils.getDs2JdbcTemplate();

    /**
     * 主数据源 EasyEntityQuery
     */
    protected final EasyEntityQuery easyEntityQuery = DataAccessUtils.getEntityQuery();

    /**
     * 第二数据源 EasyEntityQuery
     */
    protected final EasyEntityQuery ds2EntityQuery = DataAccessUtils.getDs2EntityQuery();

    /**
     * StringRedisTemplate
     */
    protected final StringRedisTemplate redisTemplate = DataAccessUtils.getStringRedisTemplate();

    /**
     * 主数据源 TransactionTemplate
     */
    protected final TransactionTemplate transactionTemplate = DataAccessUtils.getTransactionTemplate();

    /**
     * 第二数据源 TransactionTemplate
     */
    protected final TransactionTemplate ds2TransactionTemplate = DataAccessUtils.getDs2TransactionTemplate();


    /**
     * 获取实体类的泛型类型
     *
     * @return 泛型类型 Class
     */
    @SuppressWarnings("unchecked")
    protected Class<P> entityClass() {
        return (Class<P>) ResolvableType.forClass(getClass()).as(ParentService.class)
                .getGeneric(0).resolve();
    }

    /**
     * 创建实体类实例
     *
     * @return 实体类新实例
     */
    protected P createPo() {
        return BeanUtils.instantiateClass(entityClass());
    }
}
