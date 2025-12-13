package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.DeptProxy;
import io.github.lionheartlattice.entity.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 部门表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "部门表")
@Table(value = "z_dept")
@EntityProxy
@EasyAssertMessage("未找到对应的部门表信息")
public class Dept extends ParentClientEntity<Dept, DeptProxy> implements ProxyEntityAvailable<Dept, DeptProxy> {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private BigDecimal id;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal pid;

    /**
     * 默认分配角色ID
     */
    @Schema(description = "默认分配角色ID")
    private BigDecimal defaultRoleId;

    /**
     * 层级
     */
    @Schema(description = "层级")
    private Integer deep;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 是否有子级
     */
    @Schema(description = "是否有子级")
    private Boolean hasChildren;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isLock;

    /**
     * 删除标识
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.BOOLEAN)
    @Schema(description = "删除标识")
    private Boolean delFlag;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

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
     * 更新时间
     */
    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private BigDecimal tenantId;

}
