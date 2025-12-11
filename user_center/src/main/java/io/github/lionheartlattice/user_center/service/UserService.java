package io.github.lionheartlattice.user_center.service;

import com.easy.query.api.proxy.base.ClassProxy;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.base.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.util.parent.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends ParentService {

    public boolean create(UserCreatDTO dto) {
        long rows = new User().copyFrom(dto)
                              .setUpdateId(0L)
                              .insertable()
                              .executeRows();
        return isNotNull(rows); //如果操作异常会直接抛异常，或者数据库影响行数为0，因此只需要判定非空非0即可，下列的也是类似
    }

    public EasyPageResult<User> page(PageDTO dto) {
        return new User().queryable()
                         .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .where(isNotNull(dto.getSearches()), u -> {
                             for (PageDTO.InternalSearch search : dto.getSearches()) {
                                 if (search.isLike()) {
                                     u.anyColumn(search.getProperty())
                                      .like(search.getValue());
                                 } else {
                                     u.anyColumn(search.getProperty())
                                      .eq(search.getValue());  // 精确匹配
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

    public Boolean update(UserUpdateDTO dto) {
        long rows = new User().copyFrom(dto)
                              .updatable()
                              .executeRows();
        return isNotNull(rows);
    }

    public Boolean delete(List<Long> ids) {
        long rows = new User().expressionDeletable()
                              .where(u -> u.id()
                                           .in(ids))
                              .executeRows();
        return isNotNull(rows);
    }


    public Boolean saveBatch(List<UserCreatDTO> dtos) {
        List<User> entities = CopyUtil.copyList(dtos, User.class);
        Long row = transactionTemplate.execute(status -> new User().insertable(entities)
                                                                   .batch(true)
                                                                   .executeRows());
        return isNotNull(row);
    }

    public UserUpdateDTO detail(Long id) {
        return new User().queryable()
                         .whereById(id)
                         .select(u -> new ClassProxy<>(UserUpdateDTO.class).selectAll(u))
                         .singleNotNull();
    }

    public User detailWithInclude(Long id) {
        return new User().queryable()
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .whereById(id)
                         .singleNotNull();
    }
}

