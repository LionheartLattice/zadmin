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
import io.github.lionheartlattice.configuration.SnowflakeProperties.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.configuration.SnowflakeProperties.SnowflakeProperties;
import io.github.lionheartlattice.configuration.spring.DynamicBeanFactory;
import io.github.lionheartlattice.configuration.spring.DynamicDataSourceProperties;
import io.github.lionheartlattice.configuration.spring.SpringConnectionManager;
import io.github.lionheartlattice.configuration.spring.SpringDataSourceUnitFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
public class MultiDataSourceConfiguration {

    static {
        LogFactory.useCustomLogging(Slf4jImpl.class);
    }

    private final DynamicDataSourceProperties props;
    private final SnowflakeProperties snowflakeProperties;

    public MultiDataSourceConfiguration(DynamicDataSourceProperties props, SnowflakeProperties snowflakeProperties) {
        this.props = props;
        this.snowflakeProperties = snowflakeProperties;
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
            queryConfiguration.applyPrimaryKeyGenerator(new SnowflakePrimaryKeyGenerator(snowflakeProperties));

            DefaultEasyEntityQuery defaultEasyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);
            DynamicBeanFactory.registerBean(key + "EasyEntityQuery", defaultEasyEntityQuery);
            DynamicBeanFactory.registerBean(key + "TransactionManager", new DataSourceTransactionManager(source));
        });
    }

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

    public SnowflakeProperties getSnowflakeProperties() {
        return snowflakeProperties;
    }
}
