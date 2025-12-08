package io.github.lionheartlattice.configuration.util;

     import cn.hutool.core.clone.CloneRuntimeException;
     import com.fasterxml.jackson.databind.ObjectMapper;
     import org.springframework.beans.BeanUtils;
     import org.springframework.util.Assert;

     import java.io.*;
     import java.lang.reflect.Constructor;

     /**
      * 对象复制工具类
      * <p>默认方法为深拷贝，浅拷贝使用 shallow 前缀
      */
     public abstract class CopyUtil {

         private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

         private CopyUtil() {
         }

         /**
          * 深拷贝到新实例 - 自动选择策略
          */
         @SuppressWarnings("unchecked")
         public static <T> T copy(T source) {
             Assert.notNull(source, "Source must not be null");
             if (source instanceof Serializable) {
                 return (T) copyBySerializable((Serializable) source);
             }
             return copyByJackson(source, (Class<T>) source.getClass());
         }

         /**
          * 深拷贝到指定类型的新实例
          */
         public static <T> T copy(Object source, Class<T> targetClass) {
             Assert.notNull(source, "Source must not be null");
             Assert.notNull(targetClass, "Target class must not be null");
             return copyByJackson(source, targetClass);
         }

         /**
          * 深拷贝到已有实例
          */
         @SuppressWarnings("unchecked")
         public static <T> T copy(Object source, T target) {
             Assert.notNull(source, "Source must not be null");
             Assert.notNull(target, "Target must not be null");
             T deepSource = (T) copy(source);
             BeanUtils.copyProperties(deepSource, target);
             return target;
         }

         /**
          * 通过序列化深拷贝
          */
         @SuppressWarnings("unchecked")
         public static <T extends Serializable> T copyBySerializable(T source) {
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
         protected static <T> T copyByJackson(Object source, Class<T> targetClass) {
             try {
                 String json = OBJECT_MAPPER.writeValueAsString(source);
                 return OBJECT_MAPPER.readValue(json, targetClass);
             } catch (Exception e) {
                 throw new CloneRuntimeException(e);
             }
         }

         /**
          * 浅拷贝属性到新实例
          */
         public static <T> T shallowCopy(Object source, Class<T> targetClass) {
             Assert.notNull(source, "Source must not be null");
             Assert.notNull(targetClass, "Target class must not be null");
             T target = instantiate(targetClass);
             BeanUtils.copyProperties(source, target);
             return target;
         }

         /**
          * 浅拷贝属性到已有实例
          */
         public static <T> T shallowCopy(Object source, T target) {
             Assert.notNull(source, "Source must not be null");
             Assert.notNull(target, "Target must not be null");
             BeanUtils.copyProperties(source, target);
             return target;
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
