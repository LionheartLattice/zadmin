package io.github.lionheartlattice.util.parent;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.spring.DynamicBeanFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ParentController {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected EasyEntityQuery entityQuery;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    protected JdbcTemplate ds2JdbcTemplate;
    protected EasyEntityQuery ds2EntityQuery;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        // 使用 DynamicBeanFactory 代替 SpringUtils
        ConfigurableListableBeanFactory beanFactory = DynamicBeanFactory.getConfigurableBeanFactory();
        this.ds2JdbcTemplate = beanFactory.getBean("ds2JdbcTemplate", JdbcTemplate.class);
        this.ds2EntityQuery = beanFactory.getBean("ds2", EasyEntityQuery.class);
    }
}
