package io.github.lionheartlattice.util;

import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
import com.easy.query.core.metadata.ColumnMetadata;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.lang.Snowflake;

import java.io.Serializable;

/**
 * 雪花算法主键生成器
 */
public class SnowflakePrimaryKeyGenerator implements PrimaryKeyGenerator {

    private final Snowflake snowflake;

    public SnowflakePrimaryKeyGenerator() {
        // 初始化雪花算法，使用默认的workerId和datacenterId
        this.snowflake = IdUtil.getSnowflake(1, 1);
    }

    public SnowflakePrimaryKeyGenerator(long workerId, long datacenterId) {
        // 初始化雪花算法，指定workerId和datacenterId
        this.snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    }

    @Override
    public Serializable getPrimaryKey() {
        // 返回雪花算法生成的ID（转换为Long类型）
        return snowflake.nextId();
    }

    @Override
    public void setPrimaryKey(Object entity, ColumnMetadata columnMetadata) {
        // 先检查实体中是否已有主键值，如果没有则设置
        Object oldValue = columnMetadata.getGetterCaller().apply(entity);
        if (oldValue == null) {
            PrimaryKeyGenerator.super.setPrimaryKey(entity, columnMetadata);
        }
    }
}
