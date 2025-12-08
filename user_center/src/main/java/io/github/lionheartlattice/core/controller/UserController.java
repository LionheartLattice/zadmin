package io.github.lionheartlattice.core.controller;

import io.github.lionheartlattice.util.parent.ParentUtil;
import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户模块", description = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentUtil<User> {
    //    新增用户
    @PostMapping("add")
    public long add(@RequestBody UserDTO dto) {
        boolean notNull = isNotNull(dto);
        log.warn("新增用户参数：{}", notNull ? dto : "null");
        return createPo().copyFrom(dto).setUpdateId(0L).insertable().executeRows();
    }


    //列表查询
    @PostMapping("list")
    public List<User> list() {
        List<User> list1 = easyEntityQuery.queryable(User.class).toList();
        log.warn("列表查询结果：{}", isNotNull(list1) ? list1 : "null");

        List<User> list = createPo().queryable().toList();
        log.warn("列表查询结果：{}", isNotNull(list) ? list : "null");
        return list;
    }
}

