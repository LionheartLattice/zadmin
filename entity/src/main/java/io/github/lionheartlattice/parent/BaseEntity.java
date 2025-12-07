package io.github.lionheartlattice.parent;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.clone.Cloneable;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;

import java.io.*;
import java.util.Collection;
import java.util.List;

import static io.github.lionheartlattice.configuration.easyquery.DataAccessUtils.getEntityQuery;

/**
 * 实体基类，提供通用功能
 * 支持 Active Record 模式，子类可直接调用 query()、insert()、update()、delete() 等方法
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

    // ==================== Active Record 方法 ====================

    /**
     * 获取当前实体类的查询器
     * 用法: new UserEntity().query().where(u -> u.nickname().like("管理")).toList()
     *
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> query() {
        return getEntityQuery().queryable(entityClass());
    }


    /**
     * 插入当前实体到数据库
     *
     * @return 影响的行数
     */
    @SuppressWarnings("unchecked")
    public long insert() {
        return getEntityQuery().insertable((T) this).executeRows();
    }

    /**
     * 批量插入实体
     *
     * @param entities 实体集合
     * @return 影响的行数
     */
    public long insertBatch(Collection<T> entities) {
        return getEntityQuery().insertable(entities).executeRows();
    }

    /**
     * 更新当前实体（根据主键）
     *
     * @return 影响的行数
     */
    @SuppressWarnings("unchecked")
    public long update() {
        return getEntityQuery().updatable((T) this).executeRows();
    }

    /**
     * 批量更新实体
     *
     * @param entities 实体集合
     * @return 影响的行数
     */
    public long updateBatch(Collection<T> entities) {
        return getEntityQuery().updatable(entities).executeRows();
    }

    /**
     * 删除当前实体（根据主键）
     *
     * @return 影响的行数
     */
    @SuppressWarnings("unchecked")
    public long delete() {
        return getEntityQuery().deletable((T) this).executeRows();
    }

    /**
     * 批量删除实体
     *
     * @param entities 实体集合
     * @return 影响的行数
     */
    public long deleteBatch(Collection<T> entities) {
        return getEntityQuery().deletable(entities).executeRows();
    }

    /**
     * 保存实体（存在则更新，不存在则插入）
     *
     */
    @SuppressWarnings("unchecked")
    public void save() {
        getEntityQuery().savable((T) this).executeCommand();
    }

    /**
     * 批量保存实体
     *
     * @param entities 实体集合
     */
    public void saveBatch(Collection<T> entities) {
        getEntityQuery().savable(entities).executeCommand();
    }

    /**
     * 根据主键查询单个实体
     *
     * @param id 主键
     * @return 实体对象，不存在返回 null
     */
    public T findById(Object id) {
        return getEntityQuery().queryable(entityClass()).findOrNull(id);
    }

    /**
     * 根据主键查询单个实体（不存在则抛出异常）
     *
     * @param id 主键
     * @return 实体对象
     */
    public T findByIdNotNull(Object id) {
        return getEntityQuery().queryable(entityClass()).findNotNull(id);
    }

    /**
     * 查询所有数据
     *
     * @return 实体列表
     */
    public List<T> findAll() {
        return getEntityQuery().queryable(entityClass()).toList();
    }

    /**
     * 统计总数
     *
     * @return 记录数
     */
    public long count() {
        return getEntityQuery().queryable(entityClass()).count();
    }

    /**
     * 判断是否存在数据
     *
     * @return 是否存在
     */
    public boolean exists() {
        return getEntityQuery().queryable(entityClass()).any();
    }
}
