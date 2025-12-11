package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserRoleProxy;
import io.github.lionheartlattice.util.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 系统用户表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "系统用户表")
@Table(value = "z_user")
@EntityProxy
@EasyAssertMessage("未找到对应的系统用户表信息")
public class User extends ParentClientEntity<User, UserProxy> implements ProxyEntityAvailable<User, UserProxy> {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pwd;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 性别 未知-男-女
     */
    @Schema(description = "性别 未知-男-女")
    private String sex;

    /**
     * 生日
     */
    @Schema(description = "生日")
    private LocalDate birthday;

    /**
     * 头像base64
     */
    @Schema(description = "头像base64")
    private String logo;

    /**
     * 身份证
     */
    @Schema(description = "身份证")
    private String idCard;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.BOOLEAN)
    @Schema(description = "是否删除")
    private Boolean delFlag;

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
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     *
     **/
    @Navigate(value = RelationTypeEnum.ManyToMany, selfProperty = {UserProxy.Fields.id}, selfMappingProperty = {UserRoleProxy.Fields.userId}, mappingClass = UserRole.class, targetProperty = {RoleProxy.Fields.id}, targetMappingProperty = {UserRoleProxy.Fields.roleId})
    private List<Role> roleList;

}
