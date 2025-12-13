package io.github.lionheartlattice.configuration.easyquery;

import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
import com.easy.query.core.metadata.ColumnMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 基于 DECIMAL 的可读雪花算法主键生成器
 * 结构:yyyyMMddHHmmssSSS(17位时间戳) + 机器ID(4位) + 序列号(5位)
 * 示例:20250529143025123000100001
 * - 时间戳:2025-05-29 14:30:25.123
 * - 机器ID:0001
 * - 序列号:00001
 * <p>
 * 容量:支持9999个节点,每毫秒99999个ID
 */
@Component
public class SnowflakePrimaryKeyGenerator implements PrimaryKeyGenerator {

    /**
     * 日期时间格式(到毫秒)
     */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 机器ID最大值(9999)
     */
    private static final int MAX_WORKER_ID = 9999;

    /**
     * 序列号最大值(99999)
     */
    private static final int MAX_SEQUENCE = 99999;

    /**
     * 机器ID
     */
    private final int workerId;

    /**
     * 序列号
     */
    private int sequence = 0;

    /**
     * 上次生成ID的时间戳字符串
     */
    private String lastTimestamp = "";

    /**
     * 从配置文件读取机器ID
     *
     * @param workerId 机器ID(1-9999)
     */
    public SnowflakePrimaryKeyGenerator(@Value("${app.snowflake.worker-id:1}") int workerId) {
        if (workerId < 1 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("机器ID必须在1-" + MAX_WORKER_ID + "之间");
        }
        this.workerId = workerId;
    }

    /**
     * 解析时间戳(用于调试)
     *
     * @param id 主键ID
     * @return 时间戳字符串
     */
    public static String parseTimestamp(BigDecimal id) {
        String idStr = id.toPlainString();
        if (idStr.length() < 17) {
            throw new IllegalArgumentException("无效的ID格式");
        }
        String timestamp = idStr.substring(0, 17);
        return LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER)
                            .toString();
    }

    /**
     * 解析机器ID(用于调试)
     */
    public static int parseWorkerId(BigDecimal id) {
        String idStr = id.toPlainString();
        if (idStr.length() < 21) {
            throw new IllegalArgumentException("无效的ID格式");
        }
        return Integer.parseInt(idStr.substring(17, 21));
    }

    /**
     * 解析序列号(用于调试)
     */
    public static int parseSequence(BigDecimal id) {
        String idStr = id.toPlainString();
        if (idStr.length() < 26) {
            throw new IllegalArgumentException("无效的ID格式");
        }
        return Integer.parseInt(idStr.substring(21, 26));
    }

    /**
     * 生成下一个ID
     *
     * @return DECIMAL 主键值(26位)
     */
    private synchronized BigDecimal nextId() {
        String timestamp = LocalDateTime.now()
                                        .format(TIMESTAMP_FORMATTER);

        // 时钟回拨检测
        if (timestamp.compareTo(lastTimestamp) < 0) {
            throw new RuntimeException("时钟回拨,拒绝生成ID");
        }

        if (timestamp.equals(lastTimestamp)) {
            sequence = (sequence + 1) % (MAX_SEQUENCE + 1);
            if (sequence == 0) {
                // 序列号用尽,等待下一毫秒
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 拼接: 时间戳(17位) + 机器ID(4位) + 序列号(5位)
        String id = String.format("%s%04d%05d", timestamp, workerId, sequence);
        return new BigDecimal(id);
    }

    /**
     * 等待下一毫秒
     */
    private String waitNextMillis(String lastTimestamp) {
        String timestamp = LocalDateTime.now()
                                        .format(TIMESTAMP_FORMATTER);
        while (timestamp.compareTo(lastTimestamp) <= 0) {
            timestamp = LocalDateTime.now()
                                     .format(TIMESTAMP_FORMATTER);
        }
        return timestamp;
    }

    @Override
    public Serializable getPrimaryKey() {
        return nextId();
    }

    @Override
    public void setPrimaryKey(Object entity, ColumnMetadata columnMetadata) {
        Object oldValue = columnMetadata.getGetterCaller()
                                        .apply(entity);
        if (oldValue == null) {
            PrimaryKeyGenerator.super.setPrimaryKey(entity, columnMetadata);
        }
    }
}
