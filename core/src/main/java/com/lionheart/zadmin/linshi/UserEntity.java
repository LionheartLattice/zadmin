package com.lionheart.zadmin.linshi;

import lombok.Data;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.EasyAssertMessage;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统用户表 实体类。
 *
 * @author lionheart
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@Table(value = "z_user")
@EntityProxy
@EasyAssertMessage("未找到对应的用户表信息")
public class UserEntity {

    //定义枚举常量字符串
    public static final String USER_SEX_未知 = "未知";
    public static final String USER_SEX_男 = "男";
    public static final String USER_SEX_女 = "女";
    /**
     * 用户ID
     */
    @Column(primaryKey = true, value = "id")
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 头像base64
     */
    private String logo;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean delFlag;

    /**
     * 创建人ID
     */
    private Long createId;

    /**
     * 更新人ID
     */
    private Long updateId;


}
