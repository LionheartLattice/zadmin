package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.vo.UserPageVO;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.parent.ParentController;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户模块", description = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentController<UserService> {

    //    新增用户
    @PostMapping("add")
    public ApiResult<?> create(@RequestBody UserCreatDTO dto) {
        return ApiResult.success(service.create(dto));
    }


    //列表查询
    @PostMapping("list")
    public ApiResult<?> list() {
        return ApiResult.success(service.list());
    }

    //分页查询
    @PostMapping("page")
    public ApiResult<?> page(@RequestBody UserPageVO dto) {
        return ApiResult.success(service.page(dto));
    }


    @Operation(summary = "导出Excel", description = "基于列表查询结果导出Excel报表")
    @GetMapping("export")
    public void export(HttpServletResponse response) {
        ExcelExportUtil.exportWithSchema(response, "用户列表.xlsx", service.list());
    }
}
