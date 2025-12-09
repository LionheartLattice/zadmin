package io.github.lionheartlattice.util.response;

import io.github.lionheartlattice.util.response.ErrorEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带枚举的业务异常类
 * 用于封装业务异常信息，包含错误枚举
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionWithEnum extends RuntimeException {

    /**
     * 错误枚举
     */
    private ErrorEnum errorEnum;

    /**
     * 构造方法
     *
     * @param errorEnum 错误枚举
     */
    public ExceptionWithEnum(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }

    /**
     * 构造方法（带原因）
     *
     * @param errorEnum 错误枚举
     * @param cause     原因异常
     */
    public ExceptionWithEnum(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMessage(), cause);
        this.errorEnum = errorEnum;
    }
}
