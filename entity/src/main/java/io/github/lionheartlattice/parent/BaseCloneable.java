package io.github.lionheartlattice.parent;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.clone.Cloneable;

import java.io.*;

/**
 * 基础克隆类，提供克隆和序列化功能
 *
 * @param <T> 实体类型
 */
public abstract class BaseCloneable<T> implements Cloneable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final CopyOptions DEFAULT_COPY_OPTIONS = CopyOptions.create()
            .setIgnoreNullValue(true).setIgnoreError(true);

    /**
     * 通过序列化实现对象深克隆
     *
     * @param obj 待克隆对象
     * @return 深克隆后的对象
     */
    private static Object deepCloneObject(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); ObjectInputStream ois = new ObjectInputStream(bis)) {
                return ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new CloneRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneRuntimeException(e);
        }
    }

    /**
     * 通过序列化实现深克隆
     *
     * @return 深克隆后的对象
     */
    @SuppressWarnings("unchecked")
    public T deepClone() {
        return (T) deepCloneObject(this);
    }

    /**
     * 从源对象深拷贝属性到当前对象（忽略null值）
     * 先深克隆源对象，再复制属性
     *
     * @param source 源对象
     * @return 当前对象
     */
    @SuppressWarnings("unchecked")
    public T copyFrom(Object source) {
        if (source != null) {
            Object clonedSource = source instanceof Serializable ? deepCloneObject(source) : source;
            BeanUtil.copyProperties(clonedSource, this, DEFAULT_COPY_OPTIONS);
        }
        return (T) this;
    }

    /**
     * 将当前对象深拷贝属性到目标对象（忽略null值）
     *
     * @param target 目标对象
     * @param <R>    目标类型
     * @return 目标对象
     */
    public <R> R copyTo(R target) {
        if (target != null) {
            Object clonedThis = deepCloneObject(this);
            BeanUtil.copyProperties(clonedThis, target, DEFAULT_COPY_OPTIONS);
        }
        return target;
    }

    /**
     * 将当前对象深拷贝转换为指定类型的新对象
     *
     * @param targetClass 目标类型
     * @param <R>         目标类型
     * @return 新创建的目标对象
     */
    public <R> R copyTo(Class<R> targetClass) {
        Object clonedThis = deepCloneObject(this);
        return BeanUtil.copyProperties(clonedThis, targetClass);
    }

    @SuppressWarnings("unchecked")
    protected Class<T> entityClass() {
        return (Class<T>) getClass();
    }
}
