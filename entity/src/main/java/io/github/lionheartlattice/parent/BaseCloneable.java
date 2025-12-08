package io.github.lionheartlattice.parent;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.clone.Cloneable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.io.*;

public abstract class BaseCloneable<T> implements Cloneable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Object deepCloneBySerializable(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); ObjectInputStream ois = new ObjectInputStream(bis)) {
                return ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new CloneRuntimeException(e);
        }
    }

    private static <R> R deepCloneByJackson(Object obj, Class<R> targetClass) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (Exception e) {
            throw new CloneRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <R> R deepClone(Object obj, Class<R> targetClass) {
        if (obj instanceof Serializable) {
            return (R) deepCloneBySerializable(obj);
        } else {
            return deepCloneByJackson(obj, targetClass);
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

    @SuppressWarnings("unchecked")
    public T deepClone() {
        return deepClone(this, (Class<T>) this.getClass());
    }

    @SuppressWarnings("unchecked")
    public T copyFrom(Object source) {
        if (source != null) {
            Object clonedSource = deepClone(source, source.getClass());
            BeanUtils.copyProperties(clonedSource, this);
        }
        return (T) this;
    }

    public <R> R copyTo(R target) {
        if (target != null) {
            Object clonedThis = deepClone(this, this.getClass());
            BeanUtils.copyProperties(clonedThis, target);
        }
        return target;
    }

    public <R> R copyTo(Class<R> targetClass) {
        try {
            R target = targetClass.getDeclaredConstructor().newInstance();
            Object clonedThis = deepClone(this, this.getClass());
            BeanUtils.copyProperties(clonedThis, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<T> entityClass() {
        return (Class<T>) getClass();
    }
}
