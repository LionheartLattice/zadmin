package com.lionheart.zadmin.util.parent;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.lionheart.zadmin.configuration.spring.DynamicBeanFactory.getBeanFactory;

@Component
public abstract class ParentService<TProxy extends ProxyEntity<TProxy, T>, T extends ProxyEntityAvailable<T, TProxy>> {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected EasyEntityQuery entityQuery;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    protected JdbcTemplate ds2JdbcTemplate;
    protected EasyEntityQuery ds2EntityQuery;
    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public ParentService() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length > 1) {
                this.entityClass = (Class<T>) actualTypeArguments[1];
            }
        }
        if (this.entityClass == null) {
            throw new IllegalStateException("无法获取实体类类型，请检查泛型定义");
        }
    }

    @PostConstruct
    public void init() {
        // 使用 DynamicBeanFactory 代替 SpringUtils
        this.ds2JdbcTemplate = getBeanFactory().getBean("ds2JdbcTemplate", JdbcTemplate.class);
        this.ds2EntityQuery = getBeanFactory().getBean("ds2", EasyEntityQuery.class);
    }


    protected EntityQueryable<TProxy, T> query() {
        return entityQuery.queryable(entityClass);
    }

    protected EntityQueryable<TProxy, T> ds2Query() {
        return ds2EntityQuery.queryable(entityClass);
    }
}
