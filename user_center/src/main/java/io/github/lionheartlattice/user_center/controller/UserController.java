package io.github.lionheartlattice.user_center.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserPageDTO;
import io.github.lionheartlattice.entity.user_center.po.UserUpdateDTO;
import io.github.lionheartlattice.user_center.service.UserService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.ExcelImportUtil;
import io.github.lionheartlattice.util.parent.ParentController;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static io.github.lionheartlattice.configuration.easyquery.MultiDataSourceConfiguration.PRIMARY;
import static io.github.lionheartlattice.configuration.easyquery.MultiDataSourceConfiguration.TRANSACTION_MANAGER;


@Tag(name = "用户管理")
@RestController
@RequestMapping("z_user")
@RequiredArgsConstructor
public class UserController extends ParentController<UserService> {

    @PostMapping("create")
    public ApiResult<?> create(@RequestBody UserCreatDTO dto) {
        return ApiResult.success(service.create(dto));
    }

    @PostMapping("update")
    public ApiResult<?> update(@RequestBody UserUpdateDTO dto) {
        return ApiResult.success(service.update(dto));
    }

    @PostMapping("page")
    public ApiResult<?> page(@RequestBody UserPageDTO dto) {
        return ApiResult.success(service.page(dto));
    }

    @PostMapping("delete")
    public ApiResult<?> delete(@RequestBody List<Long> ids) {
        return ApiResult.success(service.delete(ids));
    }

    @SneakyThrows
    @PostMapping("upload")
    @Transactional(transactionManager = PRIMARY + TRANSACTION_MANAGER)
    public ApiResult<?> upload(@RequestParam("file") MultipartFile file) {
        List<UserCreatDTO> dtos = ExcelImportUtil.importExcel(file, UserCreatDTO.class);
        return ApiResult.success(service.saveBatch(dtos));
    }

    @PostMapping("export")
    public void downLoad(@RequestBody UserPageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, UserCreatDTO.class);
        } else {
            ExcelExportUtil.export(response, service.page(dto).getData());
        }
    }

}
