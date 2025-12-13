package io.github.lionheartlattice.util;

import org.springframework.beans.BeanUtils;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * 对象复制工具类
 * <p>默认方法为浅拷贝（基于 Spring BeanUtils）
 * <p>深拷贝方法使用 copyDeep 前缀（基于 Jackson 3）
 */
public class CopyUtil {

    private CopyUtil() {
    }

    /**
     * 延迟获取 Spring 管理的 JsonMapper (Jackson 3)
     * <p>
     * Jackson 3.x 中 ObjectMapper 已重命名为 JsonMapper
     */
    private static JsonMapper getJsonMapper() {
        return SpringUtils.getBean(JsonMapper.class);
    }


    /**
     * 浅拷贝到指定类型的新实例
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        T target = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 浅拷贝到已有实例
     */
    public static <T> T copy(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 浅拷贝到已有实例，忽略指定属性
     */
    public static <T> T copy(Object source, T target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    /**
     * 批量深拷贝到指定类型的新实例列表（基于 Jackson 3）
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        JsonMapper jsonMapper = getJsonMapper();
        JavaType listType = jsonMapper.getTypeFactory()
                                      .constructCollectionType(List.class, targetClass);
        String json = jsonMapper.writeValueAsString(sourceList);
        return jsonMapper.readValue(json, listType);
    }

    /**
     * 深拷贝到指定类型的新实例（基于 Jackson 3）
     */
    public static <T> T copyDeep(Object source, Class<T> targetClass) {
        JsonMapper jsonMapper = getJsonMapper();
        String json = jsonMapper.writeValueAsString(source);
        return jsonMapper.readValue(json, targetClass);
    }

    /**
     * 深拷贝到同类型新实例（基于 Jackson 3）
     */
    @SuppressWarnings("unchecked")
    public static <T> T copyDeep(T source) {
        return (T) copyDeep(source, source.getClass());
    }
}
