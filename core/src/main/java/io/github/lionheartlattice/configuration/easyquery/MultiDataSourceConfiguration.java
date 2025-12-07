package io.github.lionheartlattice.configuration.easyquery;

import cn.hutool.core.util.ReflectUtil;
import com.easy.query.api.proxy.client.DefaultEasyEntityQuery;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
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

    public MultiDataSourceConfiguration(DynamicDataSourceProperties props) {
        this.props = props;
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

            // 根据驱动类名判断数据库类型
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

            //注册雪花算法主键生成器
            QueryConfiguration queryConfiguration = easyQueryClient.getRuntimeContext()
                    .getQueryConfiguration();
            try {
                // 利用反射创建实例，避免 core 模块直接依赖 entity 模块
                Object generator = ReflectUtil.newInstance("io.github.lionheartlattice.user_center.po.SnowflakePrimaryKeyGenerator");
                if (generator instanceof PrimaryKeyGenerator) {
                    queryConfiguration.applyPrimaryKeyGenerator((PrimaryKeyGenerator) generator);
                }
            } catch (Exception e) {
                // 仅在运行时存在该类时注册，若不存在（如单独构建 core 时）则忽略或打印日志
                // System.err.println("SnowflakePrimaryKeyGenerator load failed: " + e.getMessage());
            }

            DefaultEasyEntityQuery defaultEasyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);
            DynamicBeanFactory.registerBean(key, defaultEasyEntityQuery);
            DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(source);
            DynamicBeanFactory.registerBean(key + "TransactionManager", dataSourceTransactionManager);
        });
    }

    /**
     * 创建多数据源 EasyEntityQuery Bean
     * <p>
     * 标记为 @Primary，可直接通过 EasyEntityQuery 类型注入，默认使用主数据源
     *
     * @return EasyMultiEntityQuery 实例
     */
    @Bean
    @Primary
    public EasyMultiEntityQuery easyMultiEntityQuery() {
        HashMap<String, EasyEntityQuery> extra = new HashMap<>();

        //直接从 DynamicBeanFactory 获取 Bean
        ConfigurableListableBeanFactory beanFactory = DynamicBeanFactory.getConfigurableBeanFactory();
        EasyEntityQuery easyEntityQuery = beanFactory.getBean("primary", EasyEntityQuery.class);

        props.getDynamic().keySet().forEach(key -> {
            EasyEntityQuery eq = beanFactory.getBean(key, EasyEntityQuery.class);
            extra.put(key, eq);
        });

        return new DefaultEasyMultiEntityQuery("primary", easyEntityQuery, extra);
    }
}
