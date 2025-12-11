package io.github.lionheartlattice.user_center.controller;

import com.easy.query.core.api.pagination.EasyPageResult;
import io.github.lionheartlattice.entity.base.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.ExcelImportUtil;
import io.github.lionheartlattice.util.parent.ParentController;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Tag(name = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentController {
    private final UserService userService;

    @PostMapping("create")
    public ApiResult<Boolean> create(@RequestBody UserCreatDTO dto) {
        return ApiResult.success(userService.create(dto));
    }

    @PostMapping("detail")
    public ApiResult<UserUpdateDTO> detail(@RequestParam Long id) {
        return ApiResult.success(userService.detail(id));
    }

    @PostMapping("update")
    public ApiResult<Boolean> update(@RequestBody UserUpdateDTO dto) {
        return ApiResult.success(userService.update(dto));
    }

    @PostMapping("page")
    public ApiResult<EasyPageResult<User>> page(@RequestBody PageDTO dto) {
        return ApiResult.success(userService.page(dto));
    }

    @PostMapping("delete")
    public ApiResult<Boolean> delete(@RequestBody List<Long> ids) {
        return ApiResult.success(userService.delete(ids));
    }

    @PostMapping("upload")
    public ApiResult<Boolean> upload(@RequestParam("file") MultipartFile file) {
        List<UserCreatDTO> dtos = ExcelImportUtil.importExcel(file, UserCreatDTO.class);
        return ApiResult.success(userService.saveBatch(dtos));
    }

    @PostMapping("export")
    public void downLoad(@RequestBody PageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, UserCreatDTO.class);
        } else {
            ExcelExportUtil.export(response, userService.page(dto)
                                                        .getData());
        }
    }


}
