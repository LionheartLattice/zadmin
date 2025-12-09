package io.github.lionheartlattice.controller;

import io.github.lionheartlattice.util.response.ApiResult;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常测试控制器
 * 用于测试全局异常处理返回
 */
@Tag(name = "异常测试", description = "用于测试异常返回结构")
@RestController
@RequestMapping("test/exception")
public class ExceptionTestController {

    /**
     * 测试运行时异常
     */
    @Operation(summary = "抛出运行时异常", description = "测试全局异常处理")
    @GetMapping("runtime")
    public ApiResult<Void> throwRuntimeException() {
        throw new ExceptionWithEnum(ErrorEnum.VALID_ERROR,"我的业务逻辑出现了问题");
    }

    /**
     *
     * 测试自定义业务异常
     */
    @Operation(summary = "抛出业务异常", description = "测试自定义异常返回")
    @GetMapping("business")
    public ApiResult<Void> throwBusinessException() {
        throw new IllegalArgumentException(ErrorEnum.INVALID_ID.getMessage());
    }
}
