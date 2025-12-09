package io.github.lionheartlattice.util.response;

import io.github.lionheartlattice.util.NullUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装
 *
 * @author lionheartlattice
 */
@Data
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码", example = "0000")
    private String code = "0000";

    @Schema(description = "响应信息", example = "SUCCESS")
    private String message = "SUCCESS";

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "额外参数")
    private Object param;


    public ApiResult() {
    }

    public ApiResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带数据和额外参数）
     */
    public static <T> ApiResult<T> success(T data, Object param) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        if (NullUtil.isNotNull(param)) {
            result.setParam(param);
        }
        return result;
    }

    /**
     * 错误响应（使用枚举）
     */
    public static <T> ApiResult<T> error(ErrorEnum responseEnum) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(String.valueOf(responseEnum.getCode()));
        result.setMessage(responseEnum.getMessage());
        return result;
    }

    /**
     * 错误响应（自定义code和message）
     */
    public static <T> ApiResult<T> error(String code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     *
     * 错误响应（仅message）
     */
    public static <T> ApiResult<T> error(String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode("9999");
        result.setMessage(message);
        return result;
    }
}
