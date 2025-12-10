package io.github.lionheartlattice.user_center.service;

import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.util.parent.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService extends ParentService<User> {
    public boolean create(UserDTO dto) {
        return createPo().copyFrom(dto).setUpdateId(0L).insertable().executeRows() > 0;
    }
}
