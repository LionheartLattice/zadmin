package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.vo.ChallengeInfo;
import io.github.lionheartlattice.user_center.service.CaptchaService;
import io.github.lionheartlattice.entity.parent.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 * 处理验证码相关的请求
 *
 * @author lionheart
 * @since 1.0
 */
@Tag(name = "验证码管理", description = "滑块验证码生成与验证")
@RestController
@RequestMapping("captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @Operation(summary = "获取验证码状态", description = "查询是否开启验证码功能")
    @GetMapping("/status")
    public ApiResult<Boolean> getCaptchaStatus() {
        // 默认开启验证码
        return ApiResult.success(true);
    }

    @Operation(summary = "获取认证挑战", description = "获取滑块验证码和临时密钥，用于登录前的验证")
    @PostMapping("/challenge")
    public ApiResult<ChallengeInfo> getChallenge(@RequestBody String clientId) {
        return ApiResult.success(captchaService.createChallenge(clientId));
    }
}

