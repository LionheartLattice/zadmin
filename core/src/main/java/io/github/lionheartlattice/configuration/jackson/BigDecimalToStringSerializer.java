package io.github.lionheartlattice.configuration.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * BigDecimal 转 String 序列化器
 * 解决 JavaScript Number 无法安全处理超大整数的问题
 * JS 安全整数范围: -9007199254740992 ~ 9007199254740992
 * 本项目主键为 28 位 BigDecimal，必须序列化为字符串
 */
public class BigDecimalToStringSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // 使用 toPlainString() 避免科学计数法
            gen.writeString(value.toPlainString());
        }
    }
}
