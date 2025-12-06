package com.lionheart.zadmin.controller;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lionheart.zadmin.controller.proxy.CompanyProxy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("t_company")
@EntityProxy
public class Company implements ProxyEntityAvailable<Company, CompanyProxy> {
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
}
