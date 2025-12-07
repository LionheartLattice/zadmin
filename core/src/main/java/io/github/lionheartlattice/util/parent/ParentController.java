package io.github.lionheartlattice.util.parent;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.github.lionheartlattice.configuration.spring.SpringUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;


public class ParentController {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected JdbcTemplate jdbcTemplate;
    protected EasyEntityQuery entityQuery;
    protected StringRedisTemplate stringRedisTemplate;
    protected JdbcTemplate ds2JdbcTemplate;
    protected EasyEntityQuery ds2EntityQuery;

    @PostConstruct
    public void init() {
        // 使用我们自定义的 SpringUtils 静态方法，并明确指定Bean名称
        this.jdbcTemplate = SpringUtils.getBean("primaryJdbcTemplate", JdbcTemplate.class);
        this.entityQuery = SpringUtils.getBean(EasyEntityQuery.class);
        this.stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);

        this.ds2JdbcTemplate = SpringUtils.getBean("ds2JdbcTemplate", JdbcTemplate.class);
        this.ds2EntityQuery = SpringUtils.getBean("ds2", EasyEntityQuery.class);
    }
}
