package io.github.lionheartlattice.user_center.service;

import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.util.parent.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService extends ParentService {
    public User detailWithInclude(Long id) {
        return new User().queryable()
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .whereById(id)
                         .singleNotNull();
    }

    public String login(LoginDTO dto) {
        return null;
    }
}
