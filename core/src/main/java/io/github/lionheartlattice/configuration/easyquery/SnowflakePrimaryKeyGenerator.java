package io.github.lionheartlattice.configuration.easyquery;

import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
import com.easy.query.core.metadata.ColumnMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义雪花算法主键生成器
 * 结构：1位符号位 + 44位时间戳 + 6位机器ID + 13位序列号
 * 44位 时间戳（可用约557年）
 * 6位 机器ID（支持64个节点）
 * 13位 序列号（每毫秒8192个ID）
 */
@Component
public class SnowflakePrimaryKeyGenerator implements PrimaryKeyGenerator {

    /**
     * 机器ID位数
     */
    private static final long WORKER_ID_BITS = 6L;

    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BITS = 13L;

    /**
     * 最大机器ID (63)
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 最大序列号 (8191)
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID左移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 起始时间戳
     */
    private final long epoch;

    /**
     * 机器ID
     */
    private final long workerId;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 从配置文件读取参数构造
     *
     * @param epoch    起始时间戳
     * @param workerId 机器ID
     */
    public SnowflakePrimaryKeyGenerator(
            @Value("${snowflake.epoch:1704067200000}") long epoch,
            @Value("${snowflake.worker-id:1}") long workerId) {
        this.epoch = epoch;
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("机器ID必须在0-" + MAX_WORKER_ID + "之间");
        }
        this.workerId = workerId;
    }

    /**
     * 生成下一个ID
     *
     * @return 雪花ID
     */
    private synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << TIMESTAMP_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Override
    public Serializable getPrimaryKey() {
        return nextId();
    }

    @Override
    public void setPrimaryKey(Object entity, ColumnMetadata columnMetadata) {
        Object oldValue = columnMetadata.getGetterCaller().apply(entity);
        if (oldValue == null) {
            PrimaryKeyGenerator.super.setPrimaryKey(entity, columnMetadata);
        }
    }
}
