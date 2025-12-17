package io.github.lionheartlattice.entity.user_center.login.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChallengeInfo {
    @Schema(description = "临时请求标识")
    private String requestId;
    @Schema(description = "秘钥")
    private String secretKey;
    @Schema(description = "背景图片Base64")
    private String backgroundImage;
    @Schema(description = "滑块图片Base64")
    private String sliderImage;
    @Schema(description = "滑块Y轴坐标")
    private Integer y;
}
