package io.github.lionheartlattice.entity.user_center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "登录返回结果")
public class LoginResultVO {
    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "用户信息")
    private UserWithMenu userInfo;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "用户名称")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "介绍")
    private String introduction;
}

