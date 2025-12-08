package io.github.lionheartlattice.configuration.easyquery;

import com.easy.query.api.proxy.client.DefaultEasyEntityQuery;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.jdbc.conn.ConnectionManager;
import com.easy.query.core.bootstrapper.DatabaseConfiguration;
import com.easy.query.core.bootstrapper.EasyQueryBootstrapper;
import com.easy.query.core.configuration.QueryConfiguration;
import com.easy.query.core.configuration.nameconversion.NameConversion;
import com.easy.query.core.configuration.nameconversion.impl.UnderlinedNameConversion;
import com.easy.query.core.datasource.DataSourceUnitFactory;
import com.easy.query.core.logging.LogFactory;
import com.easy.query.mysql.config.MySQLDatabaseConfiguration;
import com.easy.query.pgsql.config.PgSQLDatabaseConfiguration;
import io.github.lionheartlattice.configuration.spring.DynamicBeanFactory;
import io.github.lionheartlattice.configuration.spring.DynamicDataSourceProperties;
import io.github.lionheartlattice.configuration.spring.SpringConnectionManager;
import io.github.lionheartlattice.configuration.spring.SpringDataSourceUnitFactory;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * 多数据源配置类
 */
@Configuration
public class MultiDataSourceConfiguration {

    static {
        LogFactory.useCustomLogging(Slf4jImpl.class);
    }

    /**
     * 动态数据源配置
     */
    private final DynamicDataSourceProperties props;

    /**
     * 雪花算法主键生成器
     */
    @Getter
    private final SnowflakePrimaryKeyGenerator snowflakePrimaryKeyGenerator;

    /**
     * 构造函数注入
     *
     * @param props                        动态数据源配置
     * @param snowflakePrimaryKeyGenerator 雪花算法主键生成器
     */
    public MultiDataSourceConfiguration(DynamicDataSourceProperties props, SnowflakePrimaryKeyGenerator snowflakePrimaryKeyGenerator) {
        this.props = props;
        this.snowflakePrimaryKeyGenerator = snowflakePrimaryKeyGenerator;

        props.getDynamic().keySet().forEach(key -> {
            DataSourceProperties kp = props.getDynamic().get(key);
            DataSource source = DataSourceBuilder.create().type(kp.getType())
                    .driverClassName(kp.getDriverClassName()).url(kp.getUrl())
                    .username(kp.getUsername()).password(kp.getPassword()).build();
            DynamicBeanFactory.registerBean(key + "DataSource", source);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
            if ("primary".equals(key)) {
                DynamicBeanFactory.registerBean("jdbcTemplate", jdbcTemplate);
            }
            DynamicBeanFactory.registerBean(key + "JdbcTemplate", jdbcTemplate);

            DatabaseConfiguration databaseConfiguration;
            if (kp.getDriverClassName().contains("postgresql")) {
                databaseConfiguration = new PgSQLDatabaseConfiguration();
            } else {
                databaseConfiguration = new MySQLDatabaseConfiguration();
            }

            EasyQueryClient easyQueryClient = EasyQueryBootstrapper
                    .defaultBuilderConfiguration().setDefaultDataSource(source)
                    .replaceService(DataSourceUnitFactory.class, SpringDataSourceUnitFactory.class)
                    .replaceService(NameConversion.class, UnderlinedNameConversion.class)
                    .replaceService(ConnectionManager.class, SpringConnectionManager.class)
                    .optionConfigure(builder -> {
                        builder.setPrintSql(true);
                        builder.setPrintNavSql(true);
                    }).useDatabaseConfigure(databaseConfiguration).build();

            QueryConfiguration queryConfiguration = easyQueryClient.getRuntimeContext()
                    .getQueryConfiguration();
            queryConfiguration.applyPrimaryKeyGenerator(snowflakePrimaryKeyGenerator);

            DefaultEasyEntityQuery defaultEasyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);
            DynamicBeanFactory.registerBean(key + "EasyEntityQuery", defaultEasyEntityQuery);

            // 注册事务管理器
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(source);
            DynamicBeanFactory.registerBean(key + "TransactionManager", transactionManager);

            // 注册 TransactionTemplate
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            DynamicBeanFactory.registerBean(key + "TransactionTemplate", transactionTemplate);
        });
    }

    /**
     * 创建多数据源查询对象
     *
     * @return EasyMultiEntityQuery
     */
    @Bean
    @Primary
    public EasyMultiEntityQuery easyMultiEntityQuery() {
        HashMap<String, EasyEntityQuery> extra = new HashMap<>();
        ConfigurableListableBeanFactory beanFactory = DynamicBeanFactory.getConfigurableBeanFactory();
        EasyEntityQuery easyEntityQuery = beanFactory.getBean("primaryEasyEntityQuery", EasyEntityQuery.class);

        props.getDynamic().keySet().forEach(key -> {
            EasyEntityQuery eq = beanFactory.getBean(key + "EasyEntityQuery", EasyEntityQuery.class);
            extra.put(key, eq);
        });

        return new DefaultEasyMultiEntityQuery("primary", easyEntityQuery, extra);
    }
}
