package io.github.lionheartlattice.configuration.util;

import cn.hutool.core.clone.CloneRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import java.io.*;
import java.lang.reflect.Constructor;

/**
 * 对象复制工具类
 */
public abstract class CopyUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CopyUtil() {
    }

    /**
     * 浅拷贝属性到新实例
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        T target = instantiate(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 浅拷贝属性到已有实例
     */
    public static <T> T copy(Object source, T target) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 深拷贝 - 自动选择策略
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T source) {
        Assert.notNull(source, "Source must not be null");
        if (source instanceof Serializable) {
            return (T) deepCopyBySerializable((Serializable) source);
        }
        return deepCopyByJackson(source, (Class<T>) source.getClass());
    }

    /**
     * 深拷贝到指定类型
     */
    public static <T> T deepCopy(Object source, Class<T> targetClass) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(targetClass, "Target class must not be null");
        return deepCopyByJackson(source, targetClass);
    }

    /**
     * 通过序列化深拷贝
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopyBySerializable(T source) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                return (T) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new CloneRuntimeException(e);
        }
    }

    /**
     * 通过 Jackson 深拷贝
     */
    public static <T> T deepCopyByJackson(Object source, Class<T> targetClass) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(source);
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (Exception e) {
            throw new CloneRuntimeException(e);
        }
    }

    /**
     * 实例化对象
     */
    public static <T> T instantiate(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate: " + clazz.getName(), e);
        }
    }
}
