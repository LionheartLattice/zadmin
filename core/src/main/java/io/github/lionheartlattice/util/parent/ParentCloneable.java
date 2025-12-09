package io.github.lionheartlattice.util.parent;

import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.clone.Cloneable;
import io.github.lionheartlattice.util.CopyUtil;
import org.springframework.core.ResolvableType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 可克隆父类，提供克隆和复制功能
 *
 * @param <T> 实体类型
 */
@SuppressWarnings("unchecked")
public abstract class ParentCloneable<T> extends CloneSupport<T> implements Cloneable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 从源对象复制属性到当前对象
     *
     * @param source 源对象
     * @return 当前对象
     */
    public T copyFrom(Object source) {
        return CopyUtil.copy(source, (T) this);
    }

    /**
     * 复制当前对象属性到目标对象
     *
     * @param target 目标对象
     * @param <R>    目标类型
     * @return 目标对象
     */
    public <R> R copyTo(R target) {
        return CopyUtil.copy(this, target);
    }


    /**
     * 从源对象复制属性到当前对象，忽略指定属性
     *
     * @param source           源对象
     * @param ignoreProperties 忽略的属性名
     * @return 当前对象
     */
    public T copyFrom(Object source, String... ignoreProperties) {
        return CopyUtil.copy(source, (T) this, ignoreProperties);
    }


    /**
     * 复制当前对象属性到新创建的目标对象
     *
     * @param targetClass 目标类
     * @param <R>         目标类型
     * @return 新创建的目标对象
     */
    public <R> R copyTo(Class<R> targetClass) {
        return CopyUtil.copy(this, targetClass);
    }

    /**
     * 复制当前对象属性到目标对象，忽略指定属性
     *
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性名
     * @param <R>              目标类型
     * @return 目标对象
     */
    public <R> R copyTo(R target, String... ignoreProperties) {
        return CopyUtil.copy(this, target, ignoreProperties);
    }

    // ==================== 工具方法 ====================

    /**
     * 获取当前对象的泛型实际类型
     *
     * @return 实体类Class
     */
    public Class<T> entityClass() {
        return (Class<T>) ResolvableType.forClass(getClass()).as(ParentCloneable.class)
                .getGeneric(0).resolve();
    }


    /**
     * 创建当前对象的深拷贝副本
     *
     * @return 深拷贝副本
     */
    public T deepCopy() {
        return CopyUtil.copy(this, entityClass());
    }


}
