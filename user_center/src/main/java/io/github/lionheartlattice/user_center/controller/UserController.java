package io.github.lionheartlattice.user_center.controller;

import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserPageDTO;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.parent.ParentController;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("add")
    public ApiResult<?> create(@RequestBody UserCreatDTO dto) {
        return ApiResult.success(service.create(dto));
    }

    @PostMapping("page")
    public ApiResult<?> page(@RequestBody UserPageDTO dto) {
        return ApiResult.success(service.page(dto));
    }

    @Operation(summary = "excel")
    @PostMapping("export")
    public void export(@RequestBody UserPageDTO dto, HttpServletResponse response) {
        ExcelExportUtil.exportWithSchema(response, "用户列表.xlsx", service.page(dto).getData());
    }
}
