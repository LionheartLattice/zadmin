package com.lionheart.zadmin.configuration.spring;


import com.easy.query.core.basic.jdbc.conn.DataSourceWrapper;
import com.easy.query.core.basic.jdbc.conn.EasyConnection;
import com.easy.query.core.basic.jdbc.conn.EasyConnectionFactory;
import com.easy.query.core.basic.jdbc.conn.EasyDataSourceConnectionFactory;
import com.easy.query.core.basic.jdbc.conn.impl.DefaultConnectionManager;
import com.easy.query.core.enums.conn.ConnectionStrategyEnum;
import com.easy.query.core.exception.EasyQueryException;
import com.easy.query.core.sharding.EasyQueryDataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringConnectionManager extends DefaultConnectionManager {

    public SpringConnectionManager(EasyQueryDataSource easyDataSource, EasyConnectionFactory easyConnectionFactory, EasyDataSourceConnectionFactory easyDataSourceConnectionFactory) {
        super(easyDataSource, easyConnectionFactory, easyDataSourceConnectionFactory);
    }

    @Override
    public boolean currentThreadInTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive() || isOpenTransaction();
    }

    @Override
    public void closeEasyConnection(EasyConnection easyConnection) {
        if(easyConnection==null){
            return;
        }
        //当前没开事务,但是easy query手动开启了
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            if (super.isOpenTransaction()) {
                return;
            }
        } else {
            if (super.isOpenTransaction()) {
                throw new EasyQueryException("repeat transaction can't closed connection");
            }
        }
        DataSourceWrapper dataSourceUnit = easyDataSource.getDataSourceNotNull(easyConnection.getDataSourceName(), ConnectionStrategyEnum.ShareConnection);
        DataSourceUtils.releaseConnection(easyConnection.getConnection(), dataSourceUnit.getDataSourceUnit().getDataSource());
    }
}
