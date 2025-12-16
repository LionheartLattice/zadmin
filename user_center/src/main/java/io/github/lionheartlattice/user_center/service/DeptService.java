package io.github.lionheartlattice.user_center.service;

import com.easy.query.api.proxy.base.ClassProxy;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.DeptCreateDTO;
import io.github.lionheartlattice.entity.user_center.dto.DeptUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.Dept;
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
public class DeptService {
    private final TransactionTemplate transactionTemplate;

    public Boolean create(DeptCreateDTO dto) {
        long rows = new Dept().copyFrom(dto)
                              .insertable()
                              .executeRows();
        return isNotNull(rows);
    }

    public Boolean update(DeptUpdateDTO dto) {
        long rows = new Dept().copyFrom(dto)
                              .updatable()
                              .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                              .executeRows();
        return isNotNull(rows);
    }

    public DeptUpdateDTO getById(BigDecimal id) {
        return new Dept().queryable()
                         .whereById(id)
                         .select(d -> new ClassProxy<>(DeptUpdateDTO.class).selectAll(d))
                         .singleNotNull();
    }

    public EasyPageResult<Dept> page(PageDTO dto) {
        return new Dept().queryable()
                         .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                         .where(isNotNull(dto.getSearches()), u -> {
                             for (PageDTO.InternalSearch search : dto.getSearches()) {
                                 switch (search.getQueryType()) {
                                     case 1 -> u.anyColumn(search.getProperty())
                                                .eq(search.getValue());
                                     case 2 -> u.anyColumn(search.getProperty())
                                                .like(search.getValue());
                                     case 3 -> {
                                         // 解析日期范围
                                         LocalDateTime[] dateRange = PageDTO.parseDateRange(search.getValue());
                                         if (isNotNull(dateRange[0])) {
                                             u.anyColumn(search.getProperty())
                                              .ge(dateRange[0]);
                                         }
                                         if (isNotNull(dateRange[1])) {
                                             u.anyColumn(search.getProperty())
                                              .le(dateRange[1]);
                                         }
                                     }
                                     default -> throw new ExceptionWithEnum(ErrorEnum.VALID_ERROR);
                                 }
                             }
                         })
                         .orderBy(isNotNull(dto.getOrders()), u -> {
                             for (PageDTO.InternalOrder order : dto.getOrders()) {
                                 u.anyColumn(order.getProperty())
                                  .orderBy(order.isAsc());
                             }
                         })
                         .toPageResult(dto.getPageIndex(), dto.getPageSize());
    }

    public Boolean delete(List<BigDecimal> ids) {
        long rows = new Dept().expressionDeletable()
                              .where(u -> u.id()
                                           .in(ids))
                              .executeRows();
        return isNotNull(rows);
    }

    public Boolean saveBatch(List<DeptCreateDTO> dtos) {
        List<Dept> depts = CopyUtil.copyList(dtos, Dept.class);
        Long row = transactionTemplate.execute(status -> new Dept().insertable(depts)
                                                                   .batch(true)
                                                                   .executeRows());
        return isNotNull(row);
    }
}
