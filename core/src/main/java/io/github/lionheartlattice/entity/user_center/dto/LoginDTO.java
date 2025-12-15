package io.github.lionheartlattice.entity.user_center.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String clientId;
    private String requestId;
    private Integer moveX;
    private String iv;
    private String grantType;
}
