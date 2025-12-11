package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserDeptProxy;
import io.github.lionheartlattice.util.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户部门关联表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "用户部门关联表")
@Table(value = "z_user_dept")
@EntityProxy
@EasyAssertMessage("未找到对应的用户部门关联表信息")
public class UserDept extends ParentClientEntity<UserDept, UserDeptProxy> implements ProxyEntityAvailable<UserDept, UserDeptProxy> {

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
     * 部门ID
     */
    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createId;

}
