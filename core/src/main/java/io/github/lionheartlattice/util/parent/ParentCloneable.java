package io.github.lionheartlattice.util.parent;

import cn.hutool.core.clone.CloneRuntimeException;
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
public abstract class ParentCloneable<T> implements Cloneable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneRuntimeException(e);
        }
    }

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

    public <R> R cloneTo(R target) {
        return CopyUtil.copyShallow(this.clone(), target);
    }

    public <R> R cloneTo(Class<R> targetClass) {
        return CopyUtil.copyShallow(this.clone(), targetClass);
    }

    @SuppressWarnings("unchecked")
    public Class<T> entityClass() {
        return (Class<T>) ResolvableType.forClass(getClass()).as(ParentCloneable.class)
                .getGeneric(0).resolve();
    }
}
