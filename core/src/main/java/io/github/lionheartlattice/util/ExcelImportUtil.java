package io.github.lionheartlattice.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel 导入工具：表头基于 @Schema.description 匹配字段。
 */
public class ExcelImportUtil {

    private ExcelImportUtil() {
    }

    /**
     * 从 MultipartFile 导入并转为实体列表。
     *
     * @param file  上传的 Excel 文件
     * @param clazz 目标类型，字段需使用 @Schema(description="表头名")
     */
    @SneakyThrows
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) {
        try (InputStream in = file.getInputStream()) {
            return importExcel(in, clazz);
        }
    }

    /**
     * 从输入流导入并转为实体列表。
     *
     * @param in    Excel 输入流
     * @param clazz 目标类型，字段需使用 @Schema(description="表头名")
     */
    @SneakyThrows
    public static <T> List<T> importExcel(InputStream in, Class<T> clazz) {
        Map<String, String> headerAlias = buildHeaderAlias(clazz);
        if (headerAlias.isEmpty()) {
            throw new IllegalArgumentException("未找到任何带 @Schema(description) 的字段");
        }

        ExcelReader reader = ExcelUtil.getReader(in);
        // Excel 表头 -> 字段名，兼容大小写
        headerAlias.forEach((header, field) -> {
            reader.addHeaderAlias(header, field);
            reader.addHeaderAlias(header.toLowerCase(Locale.ROOT), field);
            reader.addHeaderAlias(header.toUpperCase(Locale.ROOT), field);
        });
        reader.setIgnoreEmptyRow(true);          // 忽略空行

        // 可选：校验缺失表头
        Set<String> missing = detectMissingHeaders(reader, headerAlias.keySet());
        if (CollUtil.isNotEmpty(missing)) {
            throw new IllegalArgumentException("Excel 缺少表头: " + missing);
        }

        return reader.readAll(clazz);
    }

    /**
     * 遍历所有字符串字段：trim 后若为空串则设为 null。
     */
    private static <T> void trimAndBlankToNull(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Class<?> clazz = list.get(0).getClass();
        List<Field> stringFields = Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.getType() == String.class).toList();
        for (T item : list) {
            for (Field field : stringFields) {
                field.setAccessible(true);
                try {
                    String val = (String) field.get(item);
                    if (val != null) {
                        String trimmed = val.trim();
                        field.set(item, StrUtil.isEmpty(trimmed) ? null : trimmed);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("清洗字符串字段失败: " + field.getName(), e);
                }
            }
        }
    }

    /**
     * 构建“表头名 -> 字段名”映射，表头名取自 @Schema.description。
     */
    private static Map<String, String> buildHeaderAlias(Class<?> clazz) {
        Map<String, String> headerAlias = new LinkedHashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Schema schema = field.getAnnotation(Schema.class);
            if (schema != null && StrUtil.isNotBlank(schema.description())) {
                headerAlias.put(schema.description().trim(), field.getName());
            }
        }
        return headerAlias;
    }

    /**
     * 简单校验：检查必需表头是否存在。
     */
    private static Set<String> detectMissingHeaders(ExcelReader reader, Collection<String> requiredHeaders) {
        List<Object> headerRow = reader.readRow(0);
        if (CollUtil.isEmpty(headerRow)) {
            return new LinkedHashSet<>(requiredHeaders);
        }
        Set<String> actual = new HashSet<>();
        for (Object cell : headerRow) {
            if (cell != null) {
                actual.add(cell.toString().trim());
            }
        }
        // 忽略大小写比较
        Set<String> actualLower = new HashSet<>();
        actual.forEach(h -> actualLower.add(h.toLowerCase(Locale.ROOT)));

        Set<String> missing = new LinkedHashSet<>();
        for (String required : requiredHeaders) {
            if (!actualLower.contains(required.toLowerCase(Locale.ROOT))) {
                missing.add(required);
            }
        }
        return missing;
    }
}
