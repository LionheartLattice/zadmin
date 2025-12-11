package io.github.lionheartlattice.entity.user_center.po;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.configuration.easyquery.SnowflakePrimaryKeyGenerator;
import io.github.lionheartlattice.entity.user_center.po.proxy.MenuProxy;
import io.github.lionheartlattice.util.parent.ParentClientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 系统菜单表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */

@Data
@Schema(name = "系统菜单表")
@Table(value = "z_menu")
@EntityProxy
@EasyAssertMessage("未找到对应的系统菜单表信息")
public class Menu extends ParentClientEntity<Menu, MenuProxy> implements ProxyEntityAvailable<Menu, MenuProxy> {

    /**
     * 菜单表id
     */
    @Schema(description = "菜单表id", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(primaryKey = true, value = "id", primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
    private Long id;

    /**
     * 父级id
     */
    @Schema(description = "父级id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pid;

    /**
     * 路径
     */
    @Schema(description = "路径")
    private String path;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String name;

    /**
     * 标题
     */
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * icon图标
     */
    @Schema(description = "icon图标")
    private String icon;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 外链地址
     */
    @Schema(description = "外链地址")
    private String redirect;

    /**
     * 排序
     */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    /**
     * 层级
     */
    @Schema(description = "层级", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer deep;

    /**
     * 菜单类型 （字典表menu_type）
     */
    @Schema(description = "菜单类型 （字典表menu_type）")
    private String menuTypeCd;

    /**
     * 按钮权限
     */
    @Schema(description = "按钮权限")
    private String permissions;

    /**
     * 是否隐藏
     */
    @Schema(description = "是否隐藏")
    private Boolean isHidden;

    /**
     * 是否有子级
     */
    @Schema(description = "是否有子级", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean hasChildren;

    /**
     * 路由外链时填写的访问地址
     */
    @Schema(description = "路由外链时填写的访问地址")
    private Boolean isLink;

    /**
     * 菜单是否全屏
     */
    @Schema(description = "菜单是否全屏")
    private Boolean isFull;

    /**
     * 菜单是否固定在标签页
     */
    @Schema(description = "菜单是否固定在标签页")
    private Boolean isAffix;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

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
     * 是否删除
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.BOOLEAN)
    @Schema(description = "是否删除")
    private Boolean delFlag;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     *
     **/
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {MenuProxy.Fields.pid}, targetProperty = {MenuProxy.Fields.id})
    private List<Menu> menuList;

}
