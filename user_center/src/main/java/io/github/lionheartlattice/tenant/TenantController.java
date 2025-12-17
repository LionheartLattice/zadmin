package io.github.lionheartlattice.tenant;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.parent.PageResult;
import io.github.lionheartlattice.entity.user_center.tenant.TenantDTO;
import io.github.lionheartlattice.entity.parent.Views;
import io.github.lionheartlattice.entity.user_center.tenant.Tenant;
import io.github.lionheartlattice.util.ExcelExportUtil;
import io.github.lionheartlattice.util.ExcelImportUtil;
import io.github.lionheartlattice.entity.parent.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

//目前先不管权限校验，先把系统搭建快速验证
@Tag(name = "租户管理", description = "租户信息的增删改查、Excel 导入导出等操作")
@RestController
@RequestMapping("z_tenant")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @Operation(summary = "新增租户")
    @PostMapping("/create")
    public ApiResult<Boolean> create(@Validated @JsonView(Views.Create.class) @RequestBody TenantDTO dto) {
        return ApiResult.success(tenantService.create(dto));
    }

    @Operation(summary = "获取租户详情", description = "根据租户 ID 获取租户的详细信息")
    @PostMapping("/getbyid")
//    @JsonView(Views.Update.class)
//    @PreAuthorize("hasAuthority('z_tenant:getbyid')") // 权限校验
    public ApiResult<TenantDTO> getById(@RequestParam @NotNull BigDecimal id) {
        return ApiResult.success(tenantService.getById(id));
    }

    @Operation(summary = "编辑租户")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@Validated @JsonView(Views.Update.class) @RequestBody TenantDTO dto) {
        return ApiResult.success(tenantService.update(dto));
    }

    @Operation(summary = "分页查询租户", description = "支持多条件搜索、排序、分页查询租户列表")
    @PostMapping("/page")
//    @PreAuthorize("hasAuthority('z_tenant:page')") // 权限校验
    public ApiResult<PageResult<Tenant>> page(@RequestBody PageDTO dto) {
        return ApiResult.success(tenantService.page(dto));
    }

    @Operation(summary = "批量删除租户", description = "根据租户 ID 列表批量删除租户")
    @PostMapping("delete")
//    @PreAuthorize("hasAuthority('z_tenant:delete')") // 权限校验
    public ApiResult<Boolean> delete(@RequestBody @NotNull List<BigDecimal> ids) {
        return ApiResult.success(tenantService.delete(ids));
    }

    @Operation(summary = "Excel 导入租户", description = "上传 Excel 文件批量导入租户数据")
    @PostMapping("upload")
//    @PreAuthorize("hasAuthority('z_tenant:upload')") // 权限校验
    public ApiResult<Boolean> upload(@RequestParam(value = "file") @NotNull MultipartFile file) {
        List<TenantDTO> dtos = ExcelImportUtil.importExcel(file, TenantDTO.class);
        return ApiResult.success(tenantService.saveBatch(dtos));
    }

    @Operation(summary = "Excel 导出租户", description = "根据查询条件导出租户数据为 Excel 文件")
    @PostMapping("export")
//    @PreAuthorize("hasAuthority('z_tenant:export')") // 权限校验
    public void downLoad(@RequestBody PageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, TenantDTO.class);
        } else {
            ExcelExportUtil.export(response, tenantService.page(dto)
                    .getRows());
        }
    }
}
