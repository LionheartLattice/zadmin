package io.github.lionheartlattice.parent;

import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;

import java.io.Serial;

/**
 * 实体基类，提供通用功能
 * 支持 Active Record 模式，子类可直接调用 queryable()、insertable()、updatable()、deletable() 等方法
 * <p>
 * 继承层次：
 * - BaseCloneable: 提供克隆和序列化功能
 * - BaseQueryEntity: 提供 EasyBaseQuery 相关功能（原生SQL、事务、追踪静态方法、Map操作）
 * - BaseClientEntity: 提供 BaseEntityClient 相关功能（实体CRUD、关联加载、数据库管理）
 * - BaseEntity: 最终实体基类，可扩展其他通用功能
 *
 * @param <T>      实体类型
 * @param <TProxy> 对应的代理实体类型
 */
public class BaseEntity<T extends BaseEntity<T, TProxy> & ProxyEntityAvailable<T, TProxy>, TProxy extends ProxyEntity<TProxy, T>> extends BaseClientEntity<T, TProxy> {

    @Serial
    private static final long serialVersionUID = 1L;
    // 此类可扩展其他通用功能
    // 目前所有功能已在父类中实现
}
