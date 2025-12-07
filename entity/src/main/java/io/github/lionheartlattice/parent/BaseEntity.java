package io.github.lionheartlattice.parent;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.clone.Cloneable;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;

import java.io.*;

/**
 * 实体基类，提供通用功能
 *
 * @param <T>      实体类型
 * @param <TProxy> 对应的代理实体类型
 */
public class BaseEntity<T extends BaseEntity<T, TProxy> & ProxyEntityAvailable<T, TProxy>, TProxy extends ProxyEntity<TProxy, T>> implements Cloneable<T>, Serializable, ProxyEntityAvailable<T, TProxy> {

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

    /**
     * 通过序列化实现深克隆
     *
     * @return 深克隆后的对象
     */
    @SuppressWarnings("unchecked")
    public T deepClone() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            // 序列化
            oos.writeObject(this);

            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); ObjectInputStream ois = new ObjectInputStream(bis)) {

                // 反序列化
                return (T) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new CloneRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<T> entityClass() {
        return (Class<T>) getClass();
    }



}
