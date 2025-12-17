package io.github.lionheartlattice.user.service;

import com.easy.query.api.proxy.base.ClassProxy;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.user.dto.TenantDTO;
import io.github.lionheartlattice.entity.user_center.user.po.Tenant;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.configuration.exception.ErrorEnum;
import io.github.lionheartlattice.configuration.exception.ExceptionWithEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.github.lionheartlattice.util.NullUtil.isNotNull;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final TransactionTemplate transactionTemplate;

    public Boolean create(TenantDTO dto) {
        long rows = new Tenant().copyFrom(dto)
                                .insertable()
                                .executeRows();
        return isNotNull(rows);
    }

    public Boolean update(TenantDTO dto) {
        long rows = new Tenant().copyFrom(dto)
                                .updatable()
                                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                                .executeRows();
        return isNotNull(rows);
    }

    public TenantDTO getById(BigDecimal id) {
        return new Tenant().queryable()
                           .whereById(id)
                           .select(t -> new ClassProxy<>(TenantDTO.class).selectAll(t))
                           .singleNotNull();
    }

    public EasyPageResult<Tenant> page(PageDTO dto) {
        return new Tenant().queryable()
                           .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                           .where(isNotNull(dto.getSearches()), t -> {
                               for (PageDTO.InternalSearch search : dto.getSearches()) {
                                   switch (search.getQueryType()) {
                                       case 1 -> t.anyColumn(search.getProperty())
                                                  .eq(search.getValue());
                                       case 2 -> t.anyColumn(search.getProperty())
                                                  .like(search.getValue());
                                       case 3 -> {
                                           // 解析日期范围
                                           LocalDateTime[] dateRange = PageDTO.parseDateRange(search.getValue());
                                           if (isNotNull(dateRange[0])) {
                                               t.anyColumn(search.getProperty())
                                                .ge(dateRange[0]);
                                           }
                                           if (isNotNull(dateRange[1])) {
                                               t.anyColumn(search.getProperty())
                                                .le(dateRange[1]);
                                           }
                                       }
                                       default -> throw new ExceptionWithEnum(ErrorEnum.VALID_ERROR);
                                   }
                               }
                           })
                           .orderBy(isNotNull(dto.getOrders()), t -> {
                               for (PageDTO.InternalOrder order : dto.getOrders()) {
                                   t.anyColumn(order.getProperty())
                                    .orderBy(order.isAsc());
                               }
                           })
                           .toPageResult(dto.getPageIndex(), dto.getPageSize());
    }

    public Boolean delete(List<BigDecimal> ids) {
        long rows = new Tenant().expressionDeletable()
                                .where(t -> t.id()
                                             .in(ids))
                                .executeRows();
        return isNotNull(rows);
    }

    public Boolean saveBatch(List<TenantDTO> dtos) {
        List<Tenant> tenants = CopyUtil.copyList(dtos, Tenant.class);
        Long row = transactionTemplate.execute(status -> new Tenant().insertable(tenants)
                                                                     .batch(true)
                                                                     .executeRows());
        return isNotNull(row);
    }
}

