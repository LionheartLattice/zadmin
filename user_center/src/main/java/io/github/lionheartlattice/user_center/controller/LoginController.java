package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.vo.ChallengeInfo;
import io.github.lionheartlattice.entity.user_center.vo.UserWithMenu;
import io.github.lionheartlattice.user_center.service.LoginService;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理", description = "处理用户登录、登出、获取当前用户信息")
@RestController
@RequestMapping("z_login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "获取认证挑战参数", description = "获取一次性requestId和临时AES密钥，用于前端加密密码")
    @GetMapping("/challenge")
    public ApiResult<ChallengeInfo> getChallenge() {
        return ApiResult.success(loginService.createChallenge());
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 token")
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody LoginDTO dto) {
        String token = loginService.login(dto);
        return ApiResult.success(token);
    }

    @Operation(summary = "用户登出", description = "删除 token，使用户下线")
    @PostMapping("/logout")
    public ApiResult<Boolean> logout() {
        String token = SecurityContextHolder.getContext()
                                            .getAuthentication()
                                            .getCredentials()
                                            .toString();
        return ApiResult.success(loginService.logout(token));
    }

    @Operation(summary = "获取当前登录用户", description = "从 Security Context 获取当前认证用户的完整信息（包含角色、部门、菜单）")
    @GetMapping("/current-user")
    public ApiResult<UserWithMenu> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getPrincipal();
        UserWithMenu user = (UserWithMenu) principal;
        return ApiResult.success(user);
    }
}
