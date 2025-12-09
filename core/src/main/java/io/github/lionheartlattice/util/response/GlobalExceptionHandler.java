package io.github.lionheartlattice.util.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获异常并返回统一ApiResult格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<String> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数校验异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<String> handleBindException(BindException e) {
        log.error("绑定异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(ErrorEnum.INVALID_ID);
        result.setData(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }

    /**
     * 处理带枚举的业务异常
     */
    @ExceptionHandler(ExceptionWithEnum.class)
    public ApiResult<String> handleExceptionWithEnum(ExceptionWithEnum e) {
        log.error("业务异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(e.getErrorEnum());
        result.setData(e.getDetailMessage());
        result.setParam(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage());
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(ExceptionUtil.getRootCauseMessage(e));
        return result;
    }
}
