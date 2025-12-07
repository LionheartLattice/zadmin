package io.github.lionheartlattice.configuration.spring;

import com.easy.query.core.datasource.DefaultDataSourceUnit;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SpringDataSourceUnit extends DefaultDataSourceUnit {
    public SpringDataSourceUnit(String dataSourceName, DataSource dataSource, int mergePoolSize, boolean warningBusy) {
        super(dataSourceName, dataSource, mergePoolSize, warningBusy);
    }

    @Override
    protected Connection getConnection(boolean concurrency) throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }
}
