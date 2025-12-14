package io.github.lionheartlattice.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 * 用于便捷抛出异常，支持默认消息、自定义消息或包含实体信息的自动消息
 *
 * @author lionheart
 * @since 1.0
 */
public abstract class ThrowUtil {

    // ================== Boolean 判断 ==================

    public static void isTrueThrowException(boolean result) {
        if (result) throw new IllegalArgumentException("校验未通过(预期为假)");
    }

    public static void isTrueThrowException(boolean result, String message) {
        if (result) throw new IllegalArgumentException(message);
    }

    public static void isNotTrueThrowException(boolean result) {
        if (!result) throw new IllegalArgumentException("校验未通过(预期为真)");
    }

    public static void isNotTrueThrowException(boolean result, String message) {
        if (!result) throw new IllegalArgumentException(message);
    }

    // ================== isNullThrowException (若为空/0/Blank 则抛出异常) ==================

    // String
    public static void isNullThrowException(String str) {
        if (NullUtil.isNull(str)) throw new IllegalArgumentException("字符串不能为空");
    }

    public static void isNullThrowException(String str, String message) {
        if (NullUtil.isNull(str)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(String str, Object entity) {
        if (NullUtil.isNull(str)) throwException(entity, "字符串不能为空");
    }

    // Long
    public static void isNullThrowException(Long val) {
        if (NullUtil.isNull(val)) throw new IllegalArgumentException("数值不能为空或0");
    }

    public static void isNullThrowException(Long val, String message) {
        if (NullUtil.isNull(val)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(Long val, Object entity) {
        if (NullUtil.isNull(val)) throwException(entity, "数值不能为空或0");
    }

    // long
    public static void isNullThrowException(long val) {
        if (NullUtil.isNull(val)) throw new IllegalArgumentException("数值不能为0或空");
    }

    public static void isNullThrowException(long val, String message) {
        if (NullUtil.isNull(val)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(long val, Object entity) {
        if (NullUtil.isNull(val)) throwException(entity, "数值不能为0或空");
    }

    // Collection
    public static void isNullThrowException(Collection<?> collection) {
        if (NullUtil.isNull(collection)) throw new IllegalArgumentException("集合不能为空");
    }

    public static void isNullThrowException(Collection<?> collection, String message) {
        if (NullUtil.isNull(collection)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(Collection<?> collection, Object entity) {
        if (NullUtil.isNull(collection)) throwException(entity, "集合不能为空");
    }

    // Map
    public static void isNullThrowException(Map<?, ?> map) {
        if (NullUtil.isNull(map)) throw new IllegalArgumentException("Map不能为空");
    }

    public static void isNullThrowException(Map<?, ?> map, String message) {
        if (NullUtil.isNull(map)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(Map<?, ?> map, Object entity) {
        if (NullUtil.isNull(map)) throwException(entity, "Map不能为空");
    }

    // MultipartFile
    public static void isNullThrowException(MultipartFile file) {
        if (NullUtil.isNull(file)) throw new IllegalArgumentException("文件不能为空");
    }

    public static void isNullThrowException(MultipartFile file, String message) {
        if (NullUtil.isNull(file)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(MultipartFile file, Object entity) {
        if (NullUtil.isNull(file)) throwException(entity, "文件不能为空");
    }

    // Array
    public static void isNullThrowException(Object[] arr) {
        if (NullUtil.isNull(arr)) throw new IllegalArgumentException("数组不能为空");
    }

    public static void isNullThrowException(Object[] arr, String message) {
        if (NullUtil.isNull(arr)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(Object[] arr, Object entity) {
        if (NullUtil.isNull(arr)) throwException(entity, "数组不能为空");
    }

    // Object
    public static void isNullThrowException(Object obj) {
        if (NullUtil.isNull(obj)) throw new IllegalArgumentException("对象不能为空");
    }

    public static void isNullThrowException(Object obj, String message) {
        if (NullUtil.isNull(obj)) throw new IllegalArgumentException(message);
    }

    public static void isNullThrowException(Object obj, Object entity) {
        if (NullUtil.isNull(obj)) throwException(entity, "对象不能为空");
    }

    // ================== isNotNullThrowException (若非空/非0/非Blank 则抛出异常) ==================

    // String
    public static void isNotNullThrowException(String str) {
        if (NullUtil.isNotNull(str)) throw new IllegalArgumentException("字符串必须为空");
    }

    public static void isNotNullThrowException(String str, String message) {
        if (NullUtil.isNotNull(str)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(String str, Object entity) {
        if (NullUtil.isNotNull(str)) throwException(entity, "字符串必须为空");
    }

    // Long
    public static void isNotNullThrowException(Long val) {
        if (NullUtil.isNotNull(val)) throw new IllegalArgumentException("数值必须为空或0");
    }

    public static void isNotNullThrowException(Long val, String message) {
        if (NullUtil.isNotNull(val)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(Long val, Object entity) {
        if (NullUtil.isNotNull(val)) throwException(entity, "数值必须为空或0");
    }

    // long
    public static void isNotNullThrowException(long val) {
        if (NullUtil.isNotNull(val)) throw new IllegalArgumentException("数值必须为0");
    }

    public static void isNotNullThrowException(long val, String message) {
        if (NullUtil.isNotNull(val)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(long val, Object entity) {
        if (NullUtil.isNotNull(val)) throwException(entity, "数值必须为0");
    }

    // Collection
    public static void isNotNullThrowException(Collection<?> collection) {
        if (NullUtil.isNotNull(collection)) throw new IllegalArgumentException("集合必须为空");
    }

    public static void isNotNullThrowException(Collection<?> collection, String message) {
        if (NullUtil.isNotNull(collection)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(Collection<?> collection, Object entity) {
        if (NullUtil.isNotNull(collection)) throwException(entity, "集合必须为空");
    }

    // Map
    public static void isNotNullThrowException(Map<?, ?> map) {
        if (NullUtil.isNotNull(map)) throw new IllegalArgumentException("Map必须为空");
    }

    public static void isNotNullThrowException(Map<?, ?> map, String message) {
        if (NullUtil.isNotNull(map)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(Map<?, ?> map, Object entity) {
        if (NullUtil.isNotNull(map)) throwException(entity, "Map必须为空");
    }

    // MultipartFile
    public static void isNotNullThrowException(MultipartFile file) {
        if (NullUtil.isNotNull(file)) throw new IllegalArgumentException("文件必须为空");
    }

    public static void isNotNullThrowException(MultipartFile file, String message) {
        if (NullUtil.isNotNull(file)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(MultipartFile file, Object entity) {
        if (NullUtil.isNotNull(file)) throwException(entity, "文件必须为空");
    }

    // Array
    public static void isNotNullThrowException(Object[] arr) {
        if (NullUtil.isNotNull(arr)) throw new IllegalArgumentException("数组必须为空");
    }

    public static void isNotNullThrowException(Object[] arr, String message) {
        if (NullUtil.isNotNull(arr)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(Object[] arr, Object entity) {
        if (NullUtil.isNotNull(arr)) throwException(entity, "数组必须为空");
    }

    // Object
    public static void isNotNullThrowException(Object obj) {
        if (NullUtil.isNotNull(obj)) throw new IllegalArgumentException("对象必须为空");
    }

    public static void isNotNullThrowException(Object obj, String message) {
        if (NullUtil.isNotNull(obj)) throw new IllegalArgumentException(message);
    }

    public static void isNotNullThrowException(Object obj, Object entity) {
        if (NullUtil.isNotNull(obj)) throwException(entity, "对象必须为空");
    }

    // ================== Equals/NotEquals 判断 ==================

    public static void equalsThrowException(Object obj1, Object obj2) {
        if (Objects.equals(obj1, obj2)) throw new IllegalArgumentException("校验未通过(预期不相等)");
    }

    public static void equalsThrowException(Object obj1, Object obj2, String message) {
        if (Objects.equals(obj1, obj2)) throw new IllegalArgumentException(message);
    }

    public static void equalsThrowException(Object obj1, Object obj2, Object entity) {
        if (Objects.equals(obj1, obj2)) throwException(entity, "校验未通过(预期不相等)");
    }

    public static void notEqualsThrowException(Object obj1, Object obj2) {
        if (!Objects.equals(obj1, obj2)) throw new IllegalArgumentException("校验未通过(预期相等)");
    }

    public static void notEqualsThrowException(Object obj1, Object obj2, String message) {
        if (!Objects.equals(obj1, obj2)) throw new IllegalArgumentException(message);
    }

    public static void notEqualsThrowException(Object obj1, Object obj2, Object entity) {
        if (!Objects.equals(obj1, obj2)) throwException(entity, "校验未通过(预期相等)");
    }

    // ================== 内部辅助方法 ==================

    private static void throwException(Object entity, String hint) {
        String entityStr = (entity == null) ? "null" : entity.toString();
        throw new IllegalArgumentException(entityStr + " " + hint);
    }
}
