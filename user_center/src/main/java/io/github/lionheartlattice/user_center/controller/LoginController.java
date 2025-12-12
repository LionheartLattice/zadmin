package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.LoginService;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理")
@RestController
@RequestMapping("z_login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "登录", description = "用户名密码登录，返回 token")
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody LoginDTO dto) {
        String token = loginService.login(dto);
        return ApiResult.success(token);
    }

    @Operation(summary = "登出", description = "删除 token，用户下线")
    @PostMapping("/logout")
    public ApiResult<Boolean> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        // 从 Authorization: Bearer <token> 中提取 token
        String token = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring("Bearer ".length())
                        .trim();
        }
        boolean success = loginService.logout(token);
        return ApiResult.success(success);
    }

    @Operation(summary = "获取当前登录用户", description = "从 Security Context 获取当前认证用户信息")
    @GetMapping("/current-user")
    public ApiResult<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getPrincipal();
        User user = (User) principal;
        return ApiResult.success(user);
    }
}
