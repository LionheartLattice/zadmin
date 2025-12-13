package io.github.lionheartlattice.user_center.controller;

import com.easy.query.core.api.pagination.EasyPageResult;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.ExcelImportUtil;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "用户管理", description = "用户信息的增删改查、Excel 导入导出等操作")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "新增用户", description = "创建新用户，密码会自动加盐加密存储")
    @PostMapping("create")
    public ApiResult<Boolean> create(@RequestBody UserCreatDTO dto) {
        return ApiResult.success(userService.create(dto));
    }

    @Operation(summary = "获取用户详情", description = "根据用户 ID 获取用户的详细信息")
    @PostMapping("detail")
    public ApiResult<UserUpdateDTO> getById(@RequestParam BigInteger id) {
        return ApiResult.success(userService.getById(id));
    }

    @Operation(summary = "编辑用户", description = "修改用户信息（支持修改密码）")
    @PostMapping("update")
    public ApiResult<Boolean> update(@RequestBody UserUpdateDTO dto) {
        return ApiResult.success(userService.update(dto));
    }

    @Operation(summary = "分页查询用户", description = "支持多条件搜索、排序、分页查询用户列表")
    @PostMapping("page")
    public ApiResult<EasyPageResult<User>> page(@RequestBody PageDTO dto) {
        return ApiResult.success(userService.page(dto));
    }

    @Operation(summary = "批量删除用户", description = "根据用户 ID 列表批量删除用户（逻辑删除）")
    @PostMapping("delete")
    public ApiResult<Boolean> delete(@RequestBody List<BigInteger> ids) {
        return ApiResult.success(userService.delete(ids));
    }

    @Operation(summary = "Excel 导入用户", description = "上传 Excel 文件批量导入用户数据")
    @PostMapping("upload")
    public ApiResult<Boolean> upload(@RequestParam(value = "file") MultipartFile file) {
        List<UserCreatDTO> dtos = ExcelImportUtil.importExcel(file, UserCreatDTO.class);
        return ApiResult.success(userService.saveBatch(dtos));
    }

    @Operation(summary = "Excel 导出用户", description = "根据查询条件导出用户数据为 Excel 文件")
    @PostMapping("export")
    public void downLoad(@RequestBody PageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, UserCreatDTO.class);
        } else {
            ExcelExportUtil.export(response, userService.page(dto)
                                                        .getData());
        }
    }

    @Operation(summary = "加密用户密码", description = "对指定用户的密码进行加盐加密（演示功能）")
    @PostMapping("encoderPwd")
    public ApiResult<Boolean> encoderPwd(@RequestParam BigInteger id) {
        return ApiResult.success(userService.encoderPwd(id));
    }
}
