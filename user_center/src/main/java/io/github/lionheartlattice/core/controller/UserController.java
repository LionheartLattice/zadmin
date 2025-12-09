package io.github.lionheartlattice.core.controller;

import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.util.parent.ParentUtil;
import io.github.lionheartlattice.util.response.ApiResult;
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
    public ApiResult<Boolean> add(@RequestBody UserDTO dto) {
        long rows = createPo().copyFrom(dto).setUpdateId(0L).insertable().executeRows();
        return ApiResult.success(rows > 0);
    }

    //列表查询
    @PostMapping("list")
    public ApiResult<List<User>> list() {
        return ApiResult.success(createPo().queryable().toList());
    }
}
