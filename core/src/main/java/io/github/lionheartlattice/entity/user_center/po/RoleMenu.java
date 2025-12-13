package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EasyAssertMessage;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleMenuProxy;
import io.github.lionheartlattice.entity.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 角色菜单关联表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "角色菜单关联表")
@Table(value = "z_role_menu")
@EntityProxy
@EasyAssertMessage("未找到对应的角色菜单关联表信息")
public class RoleMenu extends ParentClientEntity<RoleMenu, RoleMenuProxy> implements ProxyEntityAvailable<RoleMenu, RoleMenuProxy> {

    /**
     * 关联ID
     */
    @Schema(description = "关联ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private BigDecimal id;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal roleId;

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal menuId;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private BigDecimal createId;

}
