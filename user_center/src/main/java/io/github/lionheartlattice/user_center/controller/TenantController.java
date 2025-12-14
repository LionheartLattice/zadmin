package io.github.lionheartlattice.user_center.controller;

import com.easy.query.core.api.pagination.EasyPageResult;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.TenantCreateDTO;
import io.github.lionheartlattice.entity.user_center.dto.TenantUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.Tenant;
import io.github.lionheartlattice.user_center.service.TenantService;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.ExcelImportUtil;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "租户管理", description = "租户信息的增删改查、Excel 导入导出等操作")
@RestController
@RequestMapping("z_tenant")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @Operation(summary = "新增租户", description = "创建新租户")
    @PostMapping("/create")
    public ApiResult<Boolean> create(@RequestBody TenantCreateDTO dto) {
        return ApiResult.success(tenantService.create(dto));
    }

    @Operation(summary = "获取租户详情", description = "根据租户 ID 获取租户的详细信息")
    @PostMapping("/getbyid")
    public ApiResult<TenantUpdateDTO> getById(@RequestParam BigDecimal id) {
        return ApiResult.success(tenantService.getById(id));
    }

    @Operation(summary = "编辑租户", description = "修改租户信息")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@RequestBody TenantUpdateDTO dto) {
        return ApiResult.success(tenantService.update(dto));
    }

    @Operation(summary = "分页查询租户", description = "支持多条件搜索、排序、分页查询租户列表")
    @PostMapping("/page")
    public ApiResult<EasyPageResult<Tenant>> page(@RequestBody PageDTO dto) {
        return ApiResult.success(tenantService.page(dto));
    }

    @Operation(summary = "批量���除租户", description = "根据租户 ID 列表批量删除租户")
    @PostMapping("delete")
    public ApiResult<Boolean> delete(@RequestBody List<BigDecimal> ids) {
        return ApiResult.success(tenantService.delete(ids));
    }

    @Operation(summary = "Excel 导入租户", description = "上传 Excel 文件批量导入租户数据")
    @PostMapping("upload")
    public ApiResult<Boolean> upload(@RequestParam(value = "file") MultipartFile file) {
        List<TenantCreateDTO> dtos = ExcelImportUtil.importExcel(file, TenantCreateDTO.class);
        return ApiResult.success(tenantService.saveBatch(dtos));
    }

    @Operation(summary = "Excel 导出租户", description = "根据查询条件导出租户数据为 Excel 文件")
    @PostMapping("export")
    public void downLoad(@RequestBody PageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, TenantCreateDTO.class);
        } else {
            ExcelExportUtil.export(response, tenantService.page(dto).getData());
        }
    }
}

