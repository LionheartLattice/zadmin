package io.github.lionheartlattice.util.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<String> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(dedup(e.getMessage(), ErrorEnum.VALID_ERROR.getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    @ExceptionHandler(BindException.class)
    public ApiResult<String> handleBindException(BindException e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(dedup(e.getMessage(), ErrorEnum.VALID_ERROR.getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.INVALID_ID);
        result.setData(dedup(e.getMessage(), ErrorEnum.INVALID_ID.getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    @ExceptionHandler(ExceptionWithEnum.class)
    public ApiResult<String> handleExceptionWithEnum(ExceptionWithEnum e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(e.getErrorEnum());
        result.setData(dedup(e.getMessage(), e.getErrorEnum().getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(dedup(e.getMessage(), ErrorEnum.UNKNOWN.getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception e, HttpServletRequest request) {
        log.error(buildLocation(e), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(dedup(e.getMessage(), ErrorEnum.UNKNOWN.getMessage()));
        result.setParam(buildLocation(e));
        return result;
    }

    /**
     * 提取首个项目内栈帧位置，格式: 全类名.方法名(文件:行号) + 根因
     */
    private String buildLocation(Throwable e) {
        StackTraceElement first = findAppFrame(e);
        if (first == null) {
            return "N/A";
        }
        return String.format("%s.%s(%s:%d)      %s",
                first.getClassName(), first.getMethodName(), first.getFileName(), first.getLineNumber(),
                ExceptionUtil.getRootCauseMessage(e));
    }

    /**
     * 返回首个项目内的栈帧（按包前缀过滤，可根据需要调整）
     */
    private StackTraceElement findAppFrame(Throwable e) {
        if (e == null || e.getStackTrace() == null) {
            return null;
        }
        for (StackTraceElement ste : e.getStackTrace()) {
            if (ste.getClassName().startsWith("io.github.lionheartlattice")) {
                return ste;
            }
        }
        return e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
    }

    /**
     * 如果 data 与枚举 message 相同则置空，避免与 message 重复
     */
    private String dedup(String data, String enumMsg) {
        if (data == null) {
            return null;
        }
        return data.equals(enumMsg) ? "" : data;
    }
}
