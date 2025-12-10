package io.github.lionheartlattice.user_center.service;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.base.PageRequest;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserPageDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.UserUpdateDTO;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.util.parent.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends ParentService<User> {
    public boolean create(UserCreatDTO dto) {
        long rows = createPo().copyFrom(dto).setUpdateId(0L).insertable().executeRows();
        return isNotNull(rows); //如果操作异常会直接抛异常，或者数据库影响行数为0，因此只需要判定非空非0即可，下列的也是类似
    }

    public EasyPageResult<User> page(UserPageDTO dto) {
        return createPo().queryable().filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS).where(u -> {
            u.id().eq(dto.getId());
            u.username().contains(dto.getUsername());
            u.phone().eq(dto.getPhone());
            u.nickname().contains(dto.getNickname());
            u.sex().eq(dto.getSex());
            u.idCard().eq(dto.getIdCard());
            u.email().eq(dto.getEmail());
            u.createId().eq(dto.getCreateId());
            u.updateId().eq(dto.getUpdateId());
        }).orderBy(isNotNull(dto.getOrders()), u -> {
            for (PageRequest.InternalOrder order : dto.getOrders()) {
                u.anyColumn(order.getProperty()).orderBy(order.isAsc());
            }
        }).toPageResult(dto.getPageIndex(), dto.getPageSize());
    }

    public Boolean update(UserUpdateDTO dto) {
        long rows = createPo().copyFrom(dto).updatable().executeRows();
        return isNotNull(rows);
    }

    public Boolean delete(List<Long> ids) {
        long rows = createPo().expressionDeletable().where(u -> u.id().in(ids)).executeRows();
        return isNotNull(rows);
    }

    @Transactional
    public Boolean saveBatch(List<UserCreatDTO> dtos) {
        List<User> entities = CopyUtil.copyList(dtos, User.class);
        Long row = transactionTemplate.execute(status -> createPo().insertable(entities).batch(true).executeRows());
        return isNotNull(row);
    }
}

