package io.github.lionheartlattice.parent;

import io.github.lionheartlattice.util.NullUtil;
import org.springframework.core.ResolvableType;

/**
 * 父级工具类，提供实体类操作的通用方法
 *
 * @param <T> 实体类类型
 * @author lionheart
 * @since 1.0
 */
public abstract class ParentUtil<T> extends NullUtil {

    /**
     * 获取实体类的泛型类型
     *
     * @return 泛型类型 Class
     */
    @SuppressWarnings("unchecked")
    public Class<T> entityClass() {
        return (Class<T>) ResolvableType.forClass(getClass()).as(ParentUtil.class)
                .getGeneric(0).resolve();
    }

    /**
     * 创建实体类实例
     *
     * @return 实体类新实例
     */
    public T createPo() {
        try {
            return entityClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建实体类实例失败: " + entityClass().getName(), e);
        }
    }
}
