package com.lionheart.zadmin.test.controller;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;

import com.lionheart.zadmin.test.controller.proxy.CompanyProxy;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table("t_company")
@EntityProxy
@FieldNameConstants
public class Company implements ProxyEntityAvailable<Company , CompanyProxy> {
    /**
     * 企业id
     */
    @Column(primaryKey = true)
    private String id;
    /**
     * 企业名称
     */
    private String name;

    /**
     * 企业创建时间
     */
    private LocalDateTime createTime;

    /**
     * 注册资金
     */
    private BigDecimal registerMoney;

//    /**
//     * 企业拥有的用户
//     */
//    @Navigate(value = RelationTypeEnum.OneToMany,
//            selfProperty = {Fields.id},
//            targetProperty = {SysUser.Fields.companyId})
//    private List<SysUser> users;
}
