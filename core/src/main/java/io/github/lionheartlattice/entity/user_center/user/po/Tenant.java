package io.github.lionheartlattice.entity.user_center.user.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.parent.ParentClientEntity;
import io.github.lionheartlattice.entity.user_center.po.proxy.TenantProxy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租户表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */
@Data
@Schema(name = "租户表")
@Table(value = "z_tenant")
@EntityProxy
@EasyAssertMessage("未找到对应的租户表信息")
public class Tenant extends ParentClientEntity<Tenant, TenantProxy> implements ProxyEntityAvailable<Tenant, TenantProxy> {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private BigDecimal id;

    /**
     * 租户名称
     */
    @Schema(description = "租户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 联系人ID (管理员)
     */
    @Schema(description = "联系人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactName;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactPhone;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isLock;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    /**
     * 删除标识
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.BOOLEAN)
    @Schema(description = "删除标识")
    private Boolean delFlag;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private BigDecimal createId;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private BigDecimal updateId;

    /**
     * 超级管理员ID
     */
    @Schema(description = "超级管理员ID")
    private BigDecimal superUserId;
}
