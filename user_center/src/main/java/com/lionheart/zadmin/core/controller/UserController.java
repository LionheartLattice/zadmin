package com.lionheart.zadmin.core.controller;

import com.lionheart.zadmin.user_center.po.UserEntity;
import com.lionheart.zadmin.util.parent.ParentController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户模块", description = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentController {
    //    新增用户
    @PostMapping("add")
    public long add(UserEntity userEntity) {
        return entityQuery.insertable(userEntity).executeRows();
    }
}
