package io.github.lionheartlattice.entity.user_center.dto;

import com.easy.query.core.annotation.Column;
import com.fasterxml.jackson.annotation.JsonView;
import io.github.lionheartlattice.entity.user_center.po.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租户 DTO - 使用 Jackson 视图统一创建和更新操作
 * Create 视图：不包含 id 字段，用于新增
 * Update 视图：包含 id 字段，用于更新
 * 说明：只在 id 字段上标记 @JsonView(Views.Update.class)，其他字段不加注解，在所有视图中都可见
 * {@link Tenant}
 *
 * @author lionheart
 */
@Data
@Schema(description = "租户信息")
public class TenantDTO {

    /**
     * 租户ID - 仅在更新时需要
     */
    @JsonView(Views.Update.class)
    @Schema(description = "租户ID")
    @Column(value = "id")
    @NotNull(groups = Update.class)
    private BigDecimal id;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    @NotNull
    private String name;

    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名")
    @NotNull
    private String contactName;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    @NotNull
    private String contactPhone;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定")
    private Boolean isLock;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    @NotNull
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 验证分组：创建
     */
    public interface Create {
    }

    /**
     * 验证分组：更新
     */
    public interface Update {
    }
}

