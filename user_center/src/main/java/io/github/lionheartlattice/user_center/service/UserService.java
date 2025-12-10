package io.github.lionheartlattice.user_center.service;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.vo.UserPageVO;
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

    public Object page(UserPageVO dto) {


        EasyPageResult<User> pageResult = createPo().queryable()
                .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                .where(u -> {
                    u.id().eq(dto.getId());
                    u.username().like(dto.getUsername());
                    u.phone().eq(dto.getPhone());
                    u.nickname().like(dto.getNickname());
                    u.sex().eq(dto.getSex());
                    u.idCard().eq(dto.getIdCard());
                    u.email().like(dto.getEmail());
                    u.createId().eq(dto.getCreateId());
                    u.updateId().eq(dto.getUpdateId());
                }).toPageResult(dto.getPageIndex(), dto.getPageSize());



        return null;
    }
}
