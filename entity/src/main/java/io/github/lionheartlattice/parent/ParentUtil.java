package io.github.lionheartlattice.parent;

import org.springframework.core.ResolvableType;

/**
 * 父级工具类，提供实体类操作的通用方法
 *
 * @param <T> 实体类类型
 * @author lionheart
 * @since 1.0
 */
public abstract class ParentUtil<T> {

    /**
     * 实体类Class缓存
     */
    private Class<T> entityClass;

    /**
     * 获取实体类Class
     *
     * @return 实体类Class
     */
    @SuppressWarnings("unchecked")
    protected Class<T> entityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) ResolvableType.forClass(getClass())
                    .as(ParentUtil.class).getGeneric(0).resolve();
        }
        return entityClass;
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
