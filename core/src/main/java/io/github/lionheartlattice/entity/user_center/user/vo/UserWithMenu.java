package io.github.lionheartlattice.entity.user_center.user.vo;

import com.easy.query.core.annotation.ColumnIgnore;
import com.easy.query.core.annotation.LogicDelete;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import io.github.lionheartlattice.entity.parent.ParentCloneable;
import io.github.lionheartlattice.entity.user_center.dept.Dept;
import io.github.lionheartlattice.entity.user_center.user.po.Menu;
import io.github.lionheartlattice.entity.user_center.user.po.Role;
import io.github.lionheartlattice.entity.user_center.tenant.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
public class UserWithMenu extends ParentCloneable<UserWithMenu> {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private BigDecimal id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码")
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
    private LocalDateTime updateTime;

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
    private BigDecimal createId;

    /**
     * 更新人ID
     */
    @Schema(description = "更新人ID")
    private BigDecimal updateId;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private BigDecimal tenantId;


    @Schema(description = "访问令牌")
    @ColumnIgnore
    private String accessToken;

    private List<Dept> deptList;

    private List<Role> roleList;

    private List<Menu> menuList;

    private Tenant tenant;


}
