package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.*;
import io.github.lionheartlattice.util.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 系统角色表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "系统角色表")
@Table(value = "z_role")
@EntityProxy
@EasyAssertMessage("未找到对应的系统角色表信息")
public class Role extends ParentClientEntity<Role, RoleProxy> implements ProxyEntityAvailable<Role, RoleProxy> {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    /**
     * 简介
     */
    @Schema(description = "简介")
    private String remark;

    /**
     * 删除与否
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.BOOLEAN)
    @Schema(description = "删除与否")
    private Boolean delFlag;

    /**
     * 标识，唯一
     */
    @Schema(description = "标识，唯一")
    private String permissions;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建人ID
     */
    @Schema(description = "创建人ID")
    private Long createId;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private Long updateId;

    /**
     * 是否锁定
     */
    @Schema(description = "是否锁定")
    private Boolean isLock;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     *
     **/
    @Navigate(value = RelationTypeEnum.ManyToMany, selfProperty = {RoleProxy.Fields.id}, selfMappingProperty = {UserRoleProxy.Fields.roleId}, mappingClass = UserRole.class, targetProperty = {UserProxy.Fields.id}, targetMappingProperty = {UserRoleProxy.Fields.userId})
    private List<User> userList;

    /**
     *
     **/
    @Navigate(value = RelationTypeEnum.ManyToMany, selfProperty = {RoleProxy.Fields.id}, selfMappingProperty = {RoleMenuProxy.Fields.roleId}, mappingClass = RoleMenu.class, targetProperty = {MenuProxy.Fields.id}, targetMappingProperty = {RoleMenuProxy.Fields.menuId})
    private List<Menu> menuList;
}
