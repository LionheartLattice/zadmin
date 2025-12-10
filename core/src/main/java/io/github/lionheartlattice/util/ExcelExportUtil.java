package io.github.lionheartlattice.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.DataFormat;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Excel 导出工具：表头基于 @Schema，文本格式输出，自动列宽。
 */
public class ExcelExportUtil {

    private ExcelExportUtil() {
    }

    @SneakyThrows
    public static <T> void downloadEmpty(HttpServletResponse response, Class<T> clazz) {
        List<T> data = Stream.generate(() -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).limit(100).toList();
        export(response, "导入模板.xlsx", data);
    }

    public static <T> void export(HttpServletResponse response, List<T> data) {
        export(response, "导出列表.xlsx", data);
    }

    /**
     * 自动从 data 首元素推断类型，基于 @Schema 生成表头，文本格式输出。
     *
     * @param response HttpServletResponse
     * @param fileName 导出文件名（如：用户列表.xlsx）
     * @param data     数据列表（需至少一条用于推断类型）
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> void export(HttpServletResponse response, String fileName,   List<T> data) {
        if (NullUtil.isNull(data)) {
            throw new ExceptionWithEnum(ErrorEnum.DATA_IS_NULL);
        }
        Class<T> clazz = (Class<T>) data.get(0).getClass();
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
            // 表头+数据一起计算宽度，适配中英混排
            adjustColumnWidths(writer, headerAlias, stringRows);
            writer.flush(response.getOutputStream(), true);
        } finally {
            IoUtil.close(writer);
            IoUtil.close(response.getOutputStream());
        }
    }

    /**
     * 按表头与数据的最大展示宽度设置列宽（中文按双字节估算，留出边距）。
     */
    private static void adjustColumnWidths(ExcelWriter writer, Map<String, String> headerAlias, List<Map<String, String>> rows) {
        List<String> keys = new ArrayList<>(headerAlias.keySet());
        for (int col = 0; col < keys.size(); col++) {
            String key = keys.get(col);
            int maxUnits = displayWidth(headerAlias.get(key));
            for (Map<String, String> row : rows) {
                maxUnits = Math.max(maxUnits, displayWidth(row.get(key)));
            }
            // 缩小边距，更贴近内容
            int withMargin = maxUnits + 1;
            int poiWidth = Math.min(255 * 256, (int) (withMargin * 256 * 1.05));
            writer.getSheet().setColumnWidth(col, poiWidth);
        }
    }

    /**
     * 估算字符串显示宽度：英文≈1，中文≈2，空值给4个字符宽。
     */
    private static int displayWidth(String s) {
        if (StrUtil.isEmpty(s)) {
            return 4;
        }
        int chars = s.length();
        int bytes = s.getBytes(StandardCharsets.UTF_8).length;
        int doubleBytes = Math.max(0, bytes - chars); // 非ASCII近似双宽
        return chars + doubleBytes;
    }
}
