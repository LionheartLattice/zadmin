package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserRoleProxy;
import io.github.lionheartlattice.entity.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色关联表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "用户角色关联表")
@Table(value = "z_user_role")
@EntityProxy
@EasyAssertMessage("未找到对应的用户角色关联表信息")
public class UserRole extends ParentClientEntity<UserRole, UserRoleProxy> implements ProxyEntityAvailable<UserRole, UserRoleProxy> {

    /**
     * 关联ID
     */
    @Schema(description = "关联ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createId;

}
