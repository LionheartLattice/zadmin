package io.github.lionheartlattice.core.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.util.parent.ParentUtil;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Operation(summary = "导出用户列表Excel", description = "基于列表查询结果导出Excel报表")
    @GetMapping("export")
    public void export(HttpServletResponse response) throws IOException {
        // 查询数据
        List<User> users = createPo().queryable().toList();

        // 设置响应头
        String fileName = URLEncoder.encode("用户列表.xlsx", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        ExcelWriter writer = ExcelUtil.getWriter(true);
        try {
            // 写入数据（自动根据属性名生成表头）
            writer.write(users, true);
            writer.flush(response.getOutputStream(), true);
        } finally {
            IoUtil.close(writer);
            IoUtil.close(response.getOutputStream());
        }
    }
}
