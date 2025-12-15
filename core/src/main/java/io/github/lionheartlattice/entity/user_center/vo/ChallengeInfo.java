package io.github.lionheartlattice.entity.user_center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChallengeInfo {
    @Schema(description = "临时请求标识")
    private String requestId;
    @Schema(description = "秘钥")
    private String secretKey;
}
