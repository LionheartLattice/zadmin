package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.LoginService;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理", description = "处理用户登录、登出、获取当前用户信息")
@RestController
@RequestMapping("z_login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回 token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功，返回 token"),
            @ApiResponse(responseCode = "400", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/login")
    public ApiResult<String> login(@RequestBody LoginDTO dto) {
        String token = loginService.login(dto);
        return ApiResult.success(token);
    }

    @Operation(summary = "用户登出", description = "删除 token，使用户下线")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功"),
            @ApiResponse(responseCode = "401", description = "未登录或 token 无效")
    })
    @PostMapping("/logout")
    public ApiResult<Boolean> logout(
            @RequestHeader(
                    name = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String auth) {
        String token = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring("Bearer ".length()).trim();
        }
        boolean success = loginService.logout(token);
        return ApiResult.success(success);
    }

    @Operation(summary = "获取当前登录用户", description = "从 Security Context 获取当前认证用户的完整信息（包含角色、部门、菜单）")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "401", description = "未登录或 token 无效")
    })
    @GetMapping("/current-user")
    public ApiResult<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return ApiResult.success(user);
    }
}
