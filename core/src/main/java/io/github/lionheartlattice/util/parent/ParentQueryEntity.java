package io.github.lionheartlattice.util.parent;

import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.api.insert.map.MapClientInsertable;
import com.easy.query.core.basic.api.update.map.MapClientUpdatable;
import com.easy.query.core.basic.extension.track.EntityState;
import com.easy.query.core.basic.jdbc.parameter.SQLParameter;
import com.easy.query.core.basic.jdbc.tx.Transaction;
import com.easy.query.core.context.QueryRuntimeContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.github.lionheartlattice.util.DataAccessUtils.getEntityQuery;

/**
 * 基础查询实体类，提供 EasyBaseQuery 相关功能
 * 包含原生SQL查询、事务管理、追踪管理、Map操作等静态方法
 *
 * @param <T> 实体类型
 */
public abstract class ParentQueryEntity<T> extends ParentCloneable<T> {

    // ==================== 原生 SQL 查询方法 ====================

    /**
     * 执行原生SQL查询，返回指定类型
     *
     * @param sql   SQL语句
     * @param clazz 返回类型
     * @param <R>   返回类型泛型
     * @return 查询结果列表
     */
    protected static <R> List<R> sqlQuery(String sql, Class<R> clazz) {
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
    protected static <R> List<R> sqlQuery(String sql, Class<R> clazz, List<Object> parameters) {
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
    protected static <R> List<R> sqlEasyQuery(String sql, Class<R> clazz, List<SQLParameter> parameters) {
        return getEntityQuery().sqlEasyQuery(sql, clazz, parameters);
    }

    /**
     * 执行原生SQL查询，返回Map列表
     *
     * @param sql SQL语句
     * @return Map列表
     */
    protected static List<Map<String, Object>> sqlQueryMap(String sql) {
        return getEntityQuery().sqlQueryMap(sql);
    }

    /**
     * 执行带参数的原生SQL查询，返回Map列表
     *
     * @param sql        SQL语句
     * @param parameters 参数列表
     * @return Map列表
     */
    protected static List<Map<String, Object>> sqlQueryMap(String sql, List<Object> parameters) {
        return getEntityQuery().sqlQueryMap(sql, parameters);
    }

    // ==================== 原生 SQL 执行方法 ====================

    /**
     * 执行原生SQL（增删改）
     *
     * @param sql SQL语句
     * @return 影响的行数
     */
    protected static long sqlExecute(String sql) {
        return getEntityQuery().sqlExecute(sql);
    }

    /**
     * 执行带参数的原生SQL（增删改）
     *
     * @param sql        SQL语句
     * @param parameters 参数列表
     * @return 影响的行数
     */
    protected static long sqlExecute(String sql, List<Object> parameters) {
        return getEntityQuery().sqlExecute(sql, parameters);
    }

    // ==================== 事务方法 ====================

    /**
     * 开启事务（使用默认隔离级别）
     *
     * @return Transaction 事务对象
     */
    protected static Transaction beginTransaction() {
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
    protected static Transaction beginTransaction(Integer isolationLevel) {
        return getEntityQuery().beginTransaction(isolationLevel);
    }

    // ==================== 追踪相关静态方法 ====================

    /**
     * 添加指定实体到追踪环境
     *
     * @param entity 实体对象
     * @return true:添加成功, false:已经存在相同对象或未开启追踪
     */
    protected static boolean addTracking(Object entity) {
        return getEntityQuery().addTracking(entity);
    }

    /**
     * 从追踪环境移除指定实体
     *
     * @param entity 实体对象
     * @return 是否移除成功
     */
    protected static boolean removeTracking(Object entity) {
        return getEntityQuery().removeTracking(entity);
    }

    /**
     * 获取指定实体的追踪状态（不存在则抛出异常）
     *
     * @param entity 实体对象
     * @return EntityState 实体状态
     */
    protected static EntityState getTrackEntityStateNotNull(@NotNull Object entity) {
        return getEntityQuery().getTrackEntityStateNotNull(entity);
    }

    /**
     * 获取指定实体的追踪状态（可能返回null）
     *
     * @param entity 实体对象
     * @return EntityState 实体状态，可能为null
     */
    protected static @Nullable EntityState getTrackEntityState(@NotNull Object entity) {
        return getEntityQuery().getTrackEntityState(entity);
    }

    // ==================== Map 操作方法 ====================

    /**
     * 创建 Map 插入器
     *
     * @param map 数据Map
     * @return MapClientInsertable
     */
    protected static MapClientInsertable<Map<String, Object>> mapInsertable(Map<String, Object> map) {
        return getEntityQuery().getEasyQueryClient().mapInsertable(map);
    }

    /**
     * 创建批量 Map 插入器
     *
     * @param maps 数据Map集合
     * @return MapClientInsertable
     */
    protected static MapClientInsertable<Map<String, Object>> mapInsertable(Collection<Map<String, Object>> maps) {
        return getEntityQuery().getEasyQueryClient().mapInsertable(maps);
    }

    /**
     * 创建 Map 更新器
     *
     * @param map 数据Map
     * @return MapClientUpdatable
     */
    protected static MapClientUpdatable<Map<String, Object>> mapUpdatable(Map<String, Object> map) {
        return getEntityQuery().getEasyQueryClient().mapUpdatable(map);
    }

    /**
     * 创建批量 Map 更新器
     *
     * @param maps 数据Map集合
     * @return MapClientUpdatable
     */
    protected static MapClientUpdatable<Map<String, Object>> mapUpdatable(Collection<Map<String, Object>> maps) {
        return getEntityQuery().getEasyQueryClient().mapUpdatable(maps);
    }

    // ==================== 运行时上下文 ====================

    /**
     * 获取查询运行时上下文
     *
     * @return QueryRuntimeContext
     */
    protected static QueryRuntimeContext getRuntimeContext() {
        return getEntityQuery().getRuntimeContext();
    }

    /**
     * 获取底层 EasyQueryClient 客户端
     *
     * @return EasyQueryClient
     */
    protected static EasyQueryClient getEasyQueryClient() {
        return getEntityQuery().getEasyQueryClient();
    }

    /**
     * 对保存的对象进行主键设置
     * 如果对象的id不存在追踪上下文那么将会被视为非法id从而重新赋值
     *
     * @param entity 实体对象
     */
    protected static void saveEntitySetPrimaryKey(@NotNull Object entity) {
        getEntityQuery().saveEntitySetPrimaryKey(entity);
    }


}
