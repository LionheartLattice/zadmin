package io.github.lionheartlattice.core.controller;

import io.github.lionheartlattice.user_center.po.UserEntity;
import io.github.lionheartlattice.util.parent.ParentController;
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
public class UserController extends ParentController {
    //    新增用户
    @PostMapping("add")
    public long add(@RequestBody UserEntity userEntity) {
        return entityQuery.insertable(userEntity).executeRows();
    }

    //列表查询
    @PostMapping("list")
    public List<UserEntity> list() {
        return entityQuery.queryable(UserEntity.class).toList();
    }
}
