package io.github.lionheartlattice.user_center.po;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EasyAssertMessage;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.parent.BaseEntity;
import io.github.lionheartlattice.user_center.po.proxy.UserEntityProxy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Date;

/**
 * 系统用户表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "系统用户表")
@Table(value = "z_user")
@EntityProxy
@EasyAssertMessage("未找到对应的系统用户表信息")
public class UserEntity extends BaseEntity<UserEntity, UserEntityProxy> implements ProxyEntityAvailable<UserEntity, UserEntityProxy> {

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
    private LocalDate birthday = LocalDate.now();

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
    private Date updateTime = new Date(System.currentTimeMillis());

    /**
     * 是否删除
     */
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


}
