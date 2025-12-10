package io.github.lionheartlattice.user_center.service;

import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.util.parent.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends ParentService<User> {
    public boolean create(UserCreatDTO dto) {
        return createPo().copyFrom(dto).setUpdateId(0L).insertable().executeRows() > 0;
    }

    public List<User> list() {
        return createPo().queryable().toList();
    }

    public Object page(User dto) {
        createPo().queryable().filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                .where(u -> {u.id().eq()})
                .toPageResult()
        return null;
    }
}
