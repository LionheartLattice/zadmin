package io.github.lionheartlattice.login;

import io.github.lionheartlattice.configuration.exception.ErrorEnum;
import io.github.lionheartlattice.configuration.exception.ExceptionWithEnum;
import io.github.lionheartlattice.entity.parent.ApiResult;
import io.github.lionheartlattice.entity.user_center.user.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.user.vo.UserWithMenu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理", description = "处理用户登录、登出、获取当前用户信息")
@RestController
@RequestMapping("z_login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;


    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 token 和用户信息")
    @PostMapping("/login")
    public ApiResult<UserWithMenu> login(@RequestBody LoginDTO dto) {
        return ApiResult.success(loginService.login(dto));
    }

    @Operation(summary = "获取当前登录用户", description = "从 Security Context 获取当前认证用户的完整信息（包含角色、部门、菜单）")
    @GetMapping("/current-user")
    public ApiResult<UserWithMenu> getCurrentUser() {
        UserWithMenu user = null;
        try {
            user = (UserWithMenu) SecurityContextHolder.getContext()
                                                       .getAuthentication()
                                                       .getPrincipal();
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new ExceptionWithEnum(ErrorEnum.INVALID_TOKEN);
        }
        return ApiResult.success(user);
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
