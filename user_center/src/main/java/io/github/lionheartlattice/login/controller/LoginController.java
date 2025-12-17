package io.github.lionheartlattice.login.controller;

import io.github.lionheartlattice.entity.user_center.user.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.user.vo.UserWithMenu;
import io.github.lionheartlattice.login.service.LoginService;
import io.github.lionheartlattice.entity.parent.ApiResult;
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


    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 token 和用户信息")
    @PostMapping("/login")
    public ApiResult<UserWithMenu> login(@RequestBody LoginDTO dto) {
        return ApiResult.success(loginService.login(dto));
    }

    @Operation(summary = "用户登出", description = "删除 token，使用户下线")
    @GetMapping("/logout")
    public ApiResult<Boolean> logout() {
        String token = SecurityContextHolder.getContext()
                                            .getAuthentication()
                                            .getCredentials()
                                            .toString();
        return ApiResult.success(loginService.logout(token));
    }


}
