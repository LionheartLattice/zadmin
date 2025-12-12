package io.github.lionheartlattice.user_center.service;

import cn.hutool.crypto.digest.BCrypt;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import io.github.lionheartlattice.entity.user_center.dto.LoginDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.RoleProxy;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    public User detailWithInclude(Long id) {
        return new User().queryable()
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList, r -> r.include(RoleProxy::menuList))
                         .whereById(id)
                         .singleNotNull();
    }

    public String login(LoginDTO dto) {
        Draft2<String, String> draft = new User().queryable()
                                                 .where(u -> u.username()
                                                              .eq(dto.getUsername()))
                                                 .select(u -> Select.DRAFT.of(u.username(), u.pwd()))
                                                 .singleNotNull();
        boolean checkpwed = BCrypt.checkpw(dto.getPassword(), draft.getValue2());
        return null;
    }
}
