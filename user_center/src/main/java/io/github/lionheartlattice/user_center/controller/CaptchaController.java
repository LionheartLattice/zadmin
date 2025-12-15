package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码管理", description = "验证码相关接口")
@RestController
@RequestMapping("captcha")
@RequiredArgsConstructor
public class CaptchaController {

    @Operation(summary = "获取验证码状态", description = "查询是否开启验证码")
    @GetMapping("/status")
    public ApiResult<Boolean> getCaptchaStatus() {
        // 暂时默认开启
        return ApiResult.success(true);
    }
}

