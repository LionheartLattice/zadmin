package io.github.lionheartlattice.util.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 全局异常处理器
 * 捕获异常并返回统一ApiResult格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<String> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(dedup(e.getMessage(), ErrorEnum.VALID_ERROR.getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    @ExceptionHandler(BindException.class)
    public ApiResult<String> handleBindException(BindException e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.VALID_ERROR);
        result.setData(dedup(e.getMessage(), ErrorEnum.VALID_ERROR.getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.INVALID_ID);
        result.setData(dedup(e.getMessage(), ErrorEnum.INVALID_ID.getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    @ExceptionHandler(ExceptionWithEnum.class)
    public ApiResult<String> handleExceptionWithEnum(ExceptionWithEnum e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(e.getErrorEnum());
        result.setData(dedup(e.getMessage(), e.getErrorEnum().getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(dedup(e.getMessage(), ErrorEnum.UNKNOWN.getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception e, HttpServletRequest request) {
        log.error(buildLocation(e, request), e);
        ApiResult<String> result = ApiResult.error(ErrorEnum.UNKNOWN);
        result.setData(dedup(e.getMessage(), ErrorEnum.UNKNOWN.getMessage()));
        result.setParam(buildLocation(e, request));
        return result;
    }

    /**
     * 提取首个项目内栈帧位置，格式: 位置 + URL + 异常消息 + 根因 + Body
     */
    private String buildLocation(Throwable e, HttpServletRequest request) {
        StackTraceElement first = findAppFrame(e);
        if (first == null) {
            return "N/A";
        }
        String uri = request != null ? request.getRequestURI() : "N/A";
        String method = request != null ? request.getMethod() : "N/A";
        String body = readBody(request, 1024);
        return String.format("%s.%s(%s:%d)   +   [%s %s]   +   %s   +   %s   +   body=%s",
                             first.getClassName(), first.getMethodName(), first.getFileName(), first.getLineNumber(),
                             method, uri,
                             e.getMessage(),
                             ExceptionUtil.getRootCauseMessage(e),
                             body);
    }

    /**
     * 读取缓存的请求体（截断避免过长）
     */
    @SneakyThrows
    private String readBody(HttpServletRequest request, int maxLen) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
            return "N/A";
        }
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length == 0) {
            return "N/A";
        }
        String body = new String(buf, wrapper.getCharacterEncoding());
        return body.length() > maxLen ? body.substring(0, maxLen) + "...(truncated)" : body;
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
