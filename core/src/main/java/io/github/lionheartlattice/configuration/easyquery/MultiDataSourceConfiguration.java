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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * 多数据源配置类
 */
@Configuration
public class MultiDataSourceConfiguration {

    public static final String PRIMARY = "primary";
    public static final String DS2 = "ds2";
    public static final String DATA_SOURCE = "DataSource";
    public static final String JDBC_TEMPLATE = "JdbcTemplate";
    public static final String EASY_ENTITY_QUERY = "EasyEntityQuery";
    public static final String TRANSACTION_MANAGER = "TransactionManager";
    public static final String TRANSACTION_TEMPLATE = "TransactionTemplate";
    private static final String POSTGRESQL_DRIVER = "postgresql";
    private static final String JDBC_TEMPLATE_BEAN_NAME = "jdbcTemplate";

    static {
        LogFactory.useCustomLogging(Slf4jImpl.class);
    }

    private final DynamicDataSourceProperties props;

    @Getter
    private final SnowflakePrimaryKeyGenerator snowflakePrimaryKeyGenerator;

    public MultiDataSourceConfiguration(DynamicDataSourceProperties props, SnowflakePrimaryKeyGenerator snowflakePrimaryKeyGenerator) {
        this.props = props;
        this.snowflakePrimaryKeyGenerator = snowflakePrimaryKeyGenerator;

        props.getDynamic().keySet().forEach(key -> {
            DataSourceProperties kp = props.getDynamic().get(key);
            DataSource source = DataSourceBuilder.create().type(kp.getType())
                    .driverClassName(kp.getDriverClassName()).url(kp.getUrl())
                    .username(kp.getUsername()).password(kp.getPassword()).build();
            DynamicBeanFactory.registerBean(key + DATA_SOURCE, source);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(source);
            if (PRIMARY.equals(key)) {
                DynamicBeanFactory.registerBean(JDBC_TEMPLATE_BEAN_NAME, jdbcTemplate);
            }
            DynamicBeanFactory.registerBean(key + JDBC_TEMPLATE, jdbcTemplate);

            DatabaseConfiguration databaseConfiguration = kp.getDriverClassName()
                    .contains(POSTGRESQL_DRIVER) ? new PgSQLDatabaseConfiguration() : new MySQLDatabaseConfiguration();

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
            DynamicBeanFactory.registerBean(key + EASY_ENTITY_QUERY, defaultEasyEntityQuery);

            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(source);
            DynamicBeanFactory.registerBean(key + TRANSACTION_MANAGER, transactionManager);

            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            DynamicBeanFactory.registerBean(key + TRANSACTION_TEMPLATE, transactionTemplate);
        });
    }

    @Bean
    @Primary
    public PlatformTransactionManager platformTransactionManager() {
        ConfigurableListableBeanFactory beanFactory = DynamicBeanFactory.getConfigurableBeanFactory();
        return beanFactory.getBean(PRIMARY + TRANSACTION_MANAGER, PlatformTransactionManager.class);
    }

    @Bean
    @Primary
    public EasyMultiEntityQuery easyMultiEntityQuery() {
        HashMap<String, EasyEntityQuery> extra = new HashMap<>();
        ConfigurableListableBeanFactory beanFactory = DynamicBeanFactory.getConfigurableBeanFactory();
        EasyEntityQuery easyEntityQuery = beanFactory.getBean(PRIMARY + EASY_ENTITY_QUERY, EasyEntityQuery.class);

        props.getDynamic().keySet().forEach(key -> {
            EasyEntityQuery eq = beanFactory.getBean(key + EASY_ENTITY_QUERY, EasyEntityQuery.class);
            extra.put(key, eq);
        });

        return new DefaultEasyMultiEntityQuery(PRIMARY, easyEntityQuery, extra);
    }
}
