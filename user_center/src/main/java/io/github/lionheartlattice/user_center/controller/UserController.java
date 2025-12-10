package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.parent.ParentController;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "用户模块", description = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentController<UserService> {

    //    新增用户
    @PostMapping("add")
    public ApiResult<?> create(@RequestBody UserDTO dto) {
        return ApiResult.success(service.create(dto));
    }


//    //列表查询
//    @PostMapping("list")
//    public ApiResult<?> list() {
//        log.warn("测试");
//        return ApiResult.success(createPo().queryable().toList());
//    }
//
//    @Operation(summary = "导出用户列表Excel", description = "基于列表查询结果导出Excel报表")
//    @GetMapping("export")
//    public void export(HttpServletResponse response) {
//        List<User> users = createPo().queryable().toList();
//        ExcelExportUtil.exportWithSchema(response, "用户列表.xlsx", users);
//    }
}
