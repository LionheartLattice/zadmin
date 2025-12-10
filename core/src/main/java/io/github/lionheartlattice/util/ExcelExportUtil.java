package io.github.lionheartlattice.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.reflect.Field;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.DataFormat;

/**
 * Excel 导出工具：表头基于 @Schema，文本格式输出，自动列宽。
 */
public class ExcelExportUtil {

    @SneakyThrows
    public static <T> void exportWithSchema(HttpServletResponse response,
                                            String fileName,
                                            List<T> data,
                                            Class<T> clazz) throws IOException {
        // 构建表头别名：字段名 -> @Schema.description()
        Map<String, String> headerAlias = new LinkedHashMap<>();
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Schema schema = field.getAnnotation(Schema.class);
            if (schema != null && StrUtil.isNotBlank(schema.description())) {
                headerAlias.put(field.getName(), schema.description());
                field.setAccessible(true);
                fieldMap.put(field.getName(), field);
            }
        }

        // 日期格式化器
        DateTimeFormatter localDateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 数据转字符串（含日期格式化）
        List<Map<String, String>> stringRows = new ArrayList<>();
        for (T item : data) {
            Map<String, String> row = new LinkedHashMap<>();
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                Object val;
                try {
                    val = entry.getValue().get(item);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("读取字段失败: " + entry.getKey(), e);
                }
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

        // 响应头
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=" + encoded);

        ExcelWriter writer = ExcelUtil.getWriter(true);
        try {
            // 单元格文本格式
            DataFormat dataFormat = writer.getWorkbook().createDataFormat();
            short textFormat = dataFormat.getFormat("@");
            writer.getStyleSet().getCellStyle().setDataFormat(textFormat);
            writer.getStyleSet().getHeadCellStyle().setDataFormat(textFormat);

            writer.setHeaderAlias(headerAlias);
            writer.write(stringRows, true);
            writer.autoSizeColumnAll();
            writer.flush(response.getOutputStream(), true);
        } finally {
            IoUtil.close(writer);
            IoUtil.close(response.getOutputStream());
        }
    }

    private ExcelExportUtil() {
    }
}
