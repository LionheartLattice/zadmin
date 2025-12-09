package io.github.lionheartlattice.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;

/**
 * Null 判断工具类
 *
 * @author lionheart
 * @since 1.0
 */
public class NullUtil {

    /**
     * 判断字符串非空
     */
    public static boolean isNotNull(String str) {
        return str != null && !str.isBlank();
    }

    /**
     * 判断集合非空
     */
    public static boolean isNotNull(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 判断 Map 非空
     */
    public static boolean isNotNull(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 判断文件非空
     */
    public static boolean isNotNull(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    /**
     * 判断数组非空
     */
    public static boolean isNotNull(Object[] arr) {
        return arr != null && arr.length > 0;
    }

    /**
     * 判断对象非空
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 判断字符串为空
     */
    public static boolean isNull(String str) {
        return !isNotNull(str);
    }

    /**
     * 判断集合为空
     */
    public static boolean isNull(Collection<?> collection) {
        return !isNotNull(collection);
    }

    /**
     * 判断 Map 为空
     */
    public static boolean isNull(Map<?, ?> map) {
        return !isNotNull(map);
    }

    /**
     * 判断文件为空
     */
    public static boolean isNull(MultipartFile file) {
        return !isNotNull(file);
    }

    /**
     * 判断数组为空
     */
    public static boolean isNull(Object[] arr) {
        return !isNotNull(arr);
    }

    /**
     * 判断对象为空
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }
}
