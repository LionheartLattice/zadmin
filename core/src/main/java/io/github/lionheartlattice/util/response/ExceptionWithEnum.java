package io.github.lionheartlattice.util.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带枚举的业务异常类
 * 用于封装业务异常信息，包含错误枚举和详细消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionWithEnum extends RuntimeException {

    /**
     * 错误枚举
     */
    private ErrorEnum errorEnum;

    /**
     * 详细异常信息
     */
    private String detailMessage;

    /**
     * 构造方法
     *
     * @param errorEnum 错误枚举
     */
    public ExceptionWithEnum(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    /**
     * 构造方法（带详细消息）
     *
     * @param errorEnum     错误枚举
     * @param detailMessage 详细异常信息
     */
    public ExceptionWithEnum(ErrorEnum errorEnum, String detailMessage) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
        this.detailMessage = detailMessage;
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
