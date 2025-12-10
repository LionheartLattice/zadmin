package io.github.lionheartlattice.core.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.github.lionheartlattice.entity.user_center.dto.UserDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.util.parent.ParentUtil;
import io.github.lionheartlattice.util.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormat;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Date;


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

    @SneakyThrows
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

        // 构建表头别名：字段名 -> @Schema.description()
        Map<String, String> headerAlias = new LinkedHashMap<>();
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        for (Field field : User.class.getDeclaredFields()) {
            Schema schema = field.getAnnotation(Schema.class);
            if (schema != null && cn.hutool.core.util.StrUtil.isNotBlank(schema.description())) {
                headerAlias.put(field.getName(), schema.description());
                field.setAccessible(true);
                fieldMap.put(field.getName(), field);
            }
        }

        // 日期格式化器
        DateTimeFormatter localDateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将所有字段值转换为字符串（日期字段格式化）
        List<Map<String, String>> stringRows = new ArrayList<>();
        for (User user : users) {
            Map<String, String> row = new LinkedHashMap<>();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                Object val = entry.getValue().get(user);
                if (val instanceof LocalDate ld) {
                    row.put(entry.getKey(), ld.format(localDateFmt));
                } else if (val instanceof Date d) {
                    row.put(entry.getKey(), dateFmt.format(d));
                } else {
                    row.put(entry.getKey(), Objects.toString(val, ""));
                }
            }
            stringRows.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter(true);
        try {
            // 所有单元格按文本格式输出
            DataFormat dataFormat = writer.getWorkbook().createDataFormat();
            short textFormat = dataFormat.getFormat("@");
            writer.getStyleSet().getCellStyle().setDataFormat(textFormat);      // 内容
            writer.getStyleSet().getHeadCellStyle().setDataFormat(textFormat);  // 表头

            writer.setHeaderAlias(headerAlias);
            writer.write(stringRows, true);
            // 按内容自动调整所有列宽
            writer.autoSizeColumnAll();
            writer.flush(response.getOutputStream(), true);
        } finally {
            IoUtil.close(writer);
            IoUtil.close(response.getOutputStream());
        }
    }
}
