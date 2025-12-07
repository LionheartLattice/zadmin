package io.github.lionheartlattice.parent;

import cn.hutool.core.clone.CloneRuntimeException;
import cn.hutool.core.clone.Cloneable;
import com.easy.query.api.proxy.entity.EntityQueryProxyManager;
import com.easy.query.api.proxy.entity.delete.ExpressionDeletable;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.api.proxy.entity.update.ExpressionUpdatable;
import com.easy.query.core.basic.extension.track.EntityState;
import com.easy.query.core.basic.jdbc.parameter.SQLParameter;
import com.easy.query.core.basic.jdbc.tx.Transaction;
import com.easy.query.core.configuration.LoadIncludeConfiguration;
import com.easy.query.core.context.QueryRuntimeContext;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLFuncExpression;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.expression.parser.core.PropColumn;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.util.EasyCollectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    /**
     * 执行原生SQL查询，返回指定类型
     *
     * @param sql   SQL语句
     * @param clazz 返回类型
     * @param <R>   返回类型泛型
     * @return 查询结果列表
     */
    public static <R> List<R> sqlQuery(String sql, Class<R> clazz) {
        return getEntityQuery().sqlQuery(sql, clazz);
    }

    /**
     * 执行带参数的原生SQL查询
     *
     * @param sql        SQL语句
     * @param clazz      返回类型
     * @param parameters 参数列表
     * @param <R>        返回类型泛型
     * @return 查询结果列表
     */
    public static <R> List<R> sqlQuery(String sql, Class<R> clazz, List<Object> parameters) {
        return getEntityQuery().sqlQuery(sql, clazz, parameters);
    }

    /**
     * 执行原生SQL查询，使用SQLParameter参数
     *
     * @param sql        SQL语句
     * @param clazz      返回类型
     * @param parameters SQLParameter参数列表
     * @param <R>        返回类型泛型
     * @return 查询结果列表
     */
    public static <R> List<R> sqlEasyQuery(String sql, Class<R> clazz, List<SQLParameter> parameters) {
        return getEntityQuery().sqlEasyQuery(sql, clazz, parameters);
    }

    // ==================== Active Record 基础 CRUD 方法 ====================

    /**
     * 执行原生SQL查询，返回Map列表
     *
     * @param sql SQL语句
     * @return Map列表
     */
    public static List<Map<String, Object>> sqlQueryMap(String sql) {
        return getEntityQuery().sqlQueryMap(sql);
    }

    /**
     * 执行带参数的原生SQL查询，返回Map列表
     *
     * @param sql        SQL语句
     * @param parameters 参数列表
     * @return Map列表
     */
    public static List<Map<String, Object>> sqlQueryMap(String sql, List<Object> parameters) {
        return getEntityQuery().sqlQueryMap(sql, parameters);
    }

    /**
     * 执行原生SQL（增删改）
     *
     * @param sql SQL语句
     * @return 影响的行数
     */
    public static long sqlExecute(String sql) {
        return getEntityQuery().sqlExecute(sql);
    }

    /**
     * 执行带参数的原生SQL（增删改）
     *
     * @param sql        SQL语句
     * @param parameters 参数列表
     * @return 影响的行数
     */
    public static long sqlExecute(String sql, List<Object> parameters) {
        return getEntityQuery().sqlExecute(sql, parameters);
    }

    /**
     * 开启事务（使用默认隔离级别）
     *
     * @return Transaction 事务对象
     */
    public static Transaction beginTransaction() {
        return getEntityQuery().beginTransaction();
    }

    /**
     * 开启事务（指定隔离级别）
     * 数据库隔离级别:
     * Connection.TRANSACTION_READ_UNCOMMITTED,
     * Connection.TRANSACTION_READ_COMMITTED,
     * Connection.TRANSACTION_REPEATABLE_READ,
     * Connection.TRANSACTION_SERIALIZABLE
     *
     * @param isolationLevel 隔离级别，null表示使用默认
     * @return Transaction 事务对象
     */
    public static Transaction beginTransaction(Integer isolationLevel) {
        return getEntityQuery().beginTransaction(isolationLevel);
    }

    /**
     * 添加指定实体到追踪环境
     *
     * @param entity 实体对象
     * @return true:添加成功, false:已经存在相同对象或未开启追踪
     */
    public static boolean addTracking(Object entity) {
        return getEntityQuery().addTracking(entity);
    }

    /**
     * 从追踪环境移除指定实体
     *
     * @param entity 实体对象
     * @return 是否移除成功
     */
    public static boolean removeTracking(Object entity) {
        return getEntityQuery().removeTracking(entity);
    }

    /**
     * 获取指定实体的追踪状态（不存在则抛出异常）
     *
     * @param entity 实体对象
     * @return EntityState 实体状态
     */
    public static EntityState getTrackEntityStateNotNull(@NotNull Object entity) {
        return getEntityQuery().getTrackEntityStateNotNull(entity);
    }

    /**
     * 获取指定实体的追踪状态（可能返回null）
     *
     * @param entity 实体对象
     * @return EntityState 实体状态，可能为null
     */
    public static @Nullable EntityState getTrackEntityState(@NotNull Object entity) {
        return getEntityQuery().getTrackEntityState(entity);
    }

    /**
     * 开启一个新的追踪环境
     *
     * @param trackHandle 追踪处理函数
     * @param <R>         返回类型
     * @return 处理结果
     */
    public static <R> R trackScope(SQLFuncExpression<R> trackHandle) {
        return getEntityQuery().trackScope(trackHandle);
    }

    /**
     * 在无追踪环境下执行
     *
     * @param trackHandle 处理函数
     * @param <R>         返回类型
     * @return 处理结果
     */
    public static <R> R noTrackScope(SQLFuncExpression<R> trackHandle) {
        return getEntityQuery().noTackScope(trackHandle);
    }

    /**
     * 获取查询运行时上下文
     *
     * @return QueryRuntimeContext
     */
    public static QueryRuntimeContext getRuntimeContext() {
        return getEntityQuery().getRuntimeContext();
    }

    // ==================== 查询便捷方法 ====================

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
     * 使用原生SQL查询当前实体类型
     *
     * @param sql 原生SQL
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> query(String sql) {
        return getEntityQuery().queryable(sql, entityClass());
    }

    // ==================== 原生 SQL 方法 ====================

    /**
     * 使用带参数的原生SQL查询当前实体类型
     *
     * @param sql    原生SQL
     * @param params 参数
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> query(String sql, Collection<Object> params) {
        return getEntityQuery().queryable(sql, entityClass(), params);
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
     * 获取表达式更新器，可自定义更新条件和set值
     *
     * @return ExpressionUpdatable 表达式更新器
     */
    public ExpressionUpdatable<TProxy, T> updatable() {
        return getEntityQuery().updatable(entityClass());
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

    // ==================== 事务方法 ====================

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
     * 获取表达式删除器，可自定义删除条件
     *
     * @return ExpressionDeletable 表达式删除器
     */
    public ExpressionDeletable<TProxy, T> deletable() {
        return getEntityQuery().deletable(entityClass());
    }

    // ==================== 追踪相关方法 ====================

    /**
     * 保存实体（存在则更新，不存在则插入）
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

    /**
     * 添加当前实体到追踪环境
     * 如果当前线程未开启追踪那么添加直接忽略无效
     *
     * @return true:添加成功, false:已经存在相同对象或未开启追踪
     */
    public boolean addTracking() {
        return getEntityQuery().addTracking(this);
    }

    /**
     * 从追踪环境移除当前实体
     *
     * @return 是否移除成功
     */
    public boolean removeTracking() {
        return getEntityQuery().removeTracking(this);
    }

    /**
     * 获取当前实体的追踪状态（不存在则抛出异常）
     *
     * @return EntityState 实体状态
     */
    public EntityState getTrackEntityStateNotNull() {
        return getEntityQuery().getTrackEntityStateNotNull(this);
    }

    /**
     * 获取当前实体的追踪状态（可能返回null）
     *
     * @return EntityState 实体状态，可能为null
     */
    public @Nullable EntityState getTrackEntityState() {
        return getEntityQuery().getTrackEntityState(this);
    }

    // ==================== 关联加载方法 ====================

    /**
     * 对保存的对象进行主键设置
     * 如果对象的id不存在追踪上下文那么将会被视为非法id从而重新赋值
     */
    public void saveEntitySetPrimaryKey() {
        getEntityQuery().saveEntitySetPrimaryKey(this);
    }

    /**
     * 加载当前实体的关联属性
     *
     * @param navigateProperty 导航属性选择器
     */
    @SuppressWarnings("unchecked")
    public void loadInclude(SQLFuncExpression1<TProxy, PropColumn> navigateProperty) {
        loadInclude(Collections.singletonList((T) this), navigateProperty, null);
    }

    /**
     * 加载当前实体的关联属性（带配置）
     *
     * @param navigateProperty 导航属性选择器
     * @param configure        配置
     */
    @SuppressWarnings("unchecked")
    public void loadInclude(SQLFuncExpression1<TProxy, PropColumn> navigateProperty, SQLActionExpression1<LoadIncludeConfiguration> configure) {
        loadInclude(Collections.singletonList((T) this), navigateProperty, configure);
    }

    /**
     * 批量加载实体的关联属性
     *
     * @param entities         实体列表
     * @param navigateProperty 导航属性选择器
     */
    public void loadInclude(List<T> entities, SQLFuncExpression1<TProxy, PropColumn> navigateProperty) {
        loadInclude(entities, navigateProperty, null);
    }

    // ==================== 运行时上下文 ====================

    /**
     * 批量加载实体的关联属性（带配置）
     *
     * @param entities         实体列表
     * @param navigateProperty 导航属性选择器
     * @param configure        配置
     */
    public void loadInclude(List<T> entities, SQLFuncExpression1<TProxy, PropColumn> navigateProperty, SQLActionExpression1<LoadIncludeConfiguration> configure) {
        if (EasyCollectionUtil.isEmpty(entities)) {
            return;
        }
        TProxy tProxy = EntityQueryProxyManager.create(entityClass());
        PropColumn propColumn = navigateProperty.apply(tProxy);
        getEntityQuery().getEasyQueryClient()
                .loadInclude(entities, propColumn.getValue(), configure);
    }
}
