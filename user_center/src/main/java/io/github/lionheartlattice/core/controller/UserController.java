package io.github.lionheartlattice.core.controller;

import io.github.lionheartlattice.user_center.po.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.github.lionheartlattice.configuration.easyquery.DataAccessUtils.getEntityQuery;

@Tag(name = "用户模块", description = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController {
    //    新增用户
    @PostMapping("add")
    public long add(@RequestBody UserEntity userEntity) {
        return userEntity.insertable().executeRows();
    }

    //列表查询
    @PostMapping("list")
    public List<UserEntity> list() {
        return new UserEntity().queryable().toList();
    }
}
