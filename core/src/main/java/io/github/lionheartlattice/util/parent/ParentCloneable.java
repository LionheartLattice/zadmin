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
public abstract class ParentCloneable<T> extends CloneSupport<T> implements Cloneable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @SuppressWarnings("unchecked")
    public T copyFrom(Object source) {
        return CopyUtil.copy(source, (T) this);
    }

    public <R> R copyTo(R target) {
        return CopyUtil.copy(this, target);
    }

    public <R> R copyTo(Class<R> targetClass) {
        return CopyUtil.copy(this, targetClass);
    }


    @SuppressWarnings("unchecked")
    public Class<T> entityClass() {
        return (Class<T>) ResolvableType.forClass(getClass()).as(ParentCloneable.class)
                .getGeneric(0).resolve();
    }
}
