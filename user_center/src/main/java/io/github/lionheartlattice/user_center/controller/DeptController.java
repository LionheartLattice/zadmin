package io.github.lionheartlattice.user_center.controller;

import com.easy.query.core.api.pagination.EasyPageResult;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.DeptCreateDTO;
import io.github.lionheartlattice.entity.user_center.dto.DeptUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.Dept;
import io.github.lionheartlattice.user_center.service.DeptService;
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

@Tag(name = "部门管理", description = "部门信息的增删改查、Excel 导入导出等操作")
@RestController
@RequestMapping("z_dept")
@RequiredArgsConstructor
public class DeptController {
    private final DeptService deptService;

    @Operation(summary = "新增部门", description = "创建新部门")
    @PostMapping("/create")
    public ApiResult<Boolean> create(@RequestBody DeptCreateDTO dto) {
        return ApiResult.success(deptService.create(dto));
    }

    @Operation(summary = "获取部门详情", description = "根据部门 ID 获取部门的详细信息")
    @PostMapping("/getbyid")
    public ApiResult<DeptUpdateDTO> getById(@RequestParam BigDecimal id) {
        return ApiResult.success(deptService.getById(id));
    }

    @Operation(summary = "编辑部门", description = "修改部门信息")
    @PostMapping("/update")
    public ApiResult<Boolean> update(@RequestBody DeptUpdateDTO dto) {
        return ApiResult.success(deptService.update(dto));
    }

    @Operation(summary = "分页查询部门", description = "支持多条件搜索、排序、分页查询部门列表")
    @PostMapping("/page")
    public ApiResult<EasyPageResult<Dept>> page(@RequestBody PageDTO dto) {
        return ApiResult.success(deptService.page(dto));
    }

    @Operation(summary = "批量删除部门", description = "根据部门 ID 列表批量删除部门")
    @PostMapping("delete")
    public ApiResult<Boolean> delete(@RequestBody List<BigDecimal> ids) {
        return ApiResult.success(deptService.delete(ids));
    }

    @Operation(summary = "Excel 导入部门", description = "上传 Excel 文件批量导入部门数据")
    @PostMapping("upload")
    public ApiResult<Boolean> upload(@RequestParam(value = "file") MultipartFile file) {
        List<DeptCreateDTO> dtos = ExcelImportUtil.importExcel(file, DeptCreateDTO.class);
        return ApiResult.success(deptService.saveBatch(dtos));
    }

    @Operation(summary = "Excel 导出部门", description = "根据查询条件导出部门数据为 Excel 文件")
    @PostMapping("export")
    public void downLoad(@RequestBody PageDTO dto, HttpServletResponse response) {
        if (dto.isDownloadEmptyExcel()) {
            ExcelExportUtil.downloadEmpty(response, DeptCreateDTO.class);
        } else {
            ExcelExportUtil.export(response, deptService.page(dto)
                                                        .getData());
        }
    }
}
