package io.github.lionheartlattice.entity.parent;

import com.easy.query.api.proxy.base.MapProxy;
import com.easy.query.api.proxy.entity.EntityQueryProxyManager;
import com.easy.query.api.proxy.entity.delete.EntityDeletable;
import com.easy.query.api.proxy.entity.delete.ExpressionDeletable;
import com.easy.query.api.proxy.entity.insert.EntityInsertable;
import com.easy.query.api.proxy.entity.save.EntitySavable;
import com.easy.query.api.proxy.entity.select.EntityQueryable;
import com.easy.query.api.proxy.entity.update.EntityUpdatable;
import com.easy.query.api.proxy.entity.update.ExpressionUpdatable;
import com.easy.query.core.basic.api.database.DatabaseCodeFirst;
import com.easy.query.core.basic.extension.track.EntityState;
import com.easy.query.core.configuration.LoadIncludeConfiguration;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.lambda.SQLFuncExpression;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.expression.parser.core.PropColumn;
import com.easy.query.core.migration.MigrationEntityParser;
import com.easy.query.core.proxy.DbSet;
import com.easy.query.core.proxy.ProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.trigger.TriggerEvent;
import com.easy.query.core.util.EasyCollectionUtil;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ResolvableType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.lionheartlattice.util.DataAccessUtils.*;

/**
 * 基础客户端实体类，提供 BaseEntityClient 相关功能
 * 包含实体的CRUD操作、关联加载、数据库管理等方法
 *
 * @param <T>      实体类型
 * @param <TProxy> 对应的代理实体类型
 */
public abstract class ParentClientEntity<T extends ParentClientEntity<T, TProxy> & ProxyEntityAvailable<T, TProxy>, TProxy extends ProxyEntity<TProxy, T>> extends ParentQueryEntity<T> implements ProxyEntityAvailable<T, TProxy> {

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

    // ==================== 追踪作用域方法 ====================

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
     * 获取数据库 CodeFirst 对象
     * 用于数据库迁移和表结构管理
     *
     * @return DatabaseCodeFirst
     */
    public static DatabaseCodeFirst getDatabaseCodeFirst() {
        return getEntityQuery().getEasyQueryClient()
                               .getDatabaseCodeFirst();
    }

    // ==================== 数据库管理方法 ====================

    /**
     * 设置迁移解析器
     *
     * @param migrationParser 迁移解析器
     */
    public static void setMigrationParser(MigrationEntityParser migrationParser) {
        getEntityQuery().getEasyQueryClient()
                        .setMigrationParser(migrationParser);
    }

    /**
     * 添加触发器监听器
     *
     * @param eventConsumer 事件消费者
     */
    public static void addTriggerListener(Consumer<TriggerEvent> eventConsumer) {
        getEntityQuery().getEasyQueryClient()
                        .addTriggerListener(eventConsumer);
    }

    /**
     * 按包加载数据库实体对象
     *
     * @param packageNames 包名列表
     */
    public static void loadTableEntityByPackage(String... packageNames) {
        getEntityQuery().getEasyQueryClient()
                        .loadTableEntityByPackage(packageNames);
    }

    /**
     * 按包加载数据库实体对象并且自动执行DDL操作
     *
     * @param packageNames 包名列表
     */
    public static void syncTableByPackage(String... packageNames) {
        getEntityQuery().getEasyQueryClient()
                        .syncTableByPackage(packageNames);
    }

    /**
     * 按包加载数据库实体对象并且自动执行DDL操作（指定分组大小）
     *
     * @param groupSize    分组大小
     * @param packageNames 包名列表
     */
    public static void syncTableByPackage(int groupSize, String... packageNames) {
        getEntityQuery().getEasyQueryClient()
                        .syncTableByPackage(groupSize, packageNames);
    }

    /**
     * 创建Map查询器
     *
     * @param tableName 表名
     * @return Map查询器
     */
    public static EntityQueryable<MapProxy, Map<String, Object>> mapQueryable(String tableName) {
        return getEntityQuery().mapQueryable(tableName);
    }

    /**
     * 获取当前对象的泛型实际类型
     *
     * @return 实体类Class
     */
    @SuppressWarnings("unchecked")
    public Class<TProxy> entityTProxyClass() {
        return (Class<TProxy>) ResolvableType.forClass(getClass())
                                             .as(ParentCloneable.class)
                                             .getGeneric(0)
                                             .resolve();
    }

    // ==================== 查询方法（返回构建器） ====================

    /**
     * 获取当前实体类的查询器
     * 用法: new User().queryable().where(u -> u.nickname().like("管理")).toList()
     *
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> queryable() {
        return getEntityQuery().queryable(entityClass());
    }

    /**
     * 使用原生SQL查询当前实体类型
     *
     * @param sql 原生SQL
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> queryable(String sql) {
        return getEntityQuery().queryable(sql, entityClass());
    }

    /**
     * 使用带参数的原生SQL查询当前实体类型
     *
     * @param sql    原生SQL
     * @param params 参数
     * @return EntityQueryable 查询器
     */
    public EntityQueryable<TProxy, T> queryable(String sql, Collection<Object> params) {
        return getEntityQuery().queryable(sql, entityClass(), params);
    }

    /**
     * 使用代理对象创建查询器
     *
     * @param tProxy 代理对象
     * @return 查询器
     */
    public EntityQueryable<TProxy, T> queryable(TProxy tProxy) {
        return getEntityQuery().queryable(tProxy);
    }

    // ==================== 插入方法 ====================

    /**
     * 获取当前实体的插入器
     * 用法: userEntity.insertable().executeRows()
     *
     * @return EntityInsertable 插入器
     */
    @SuppressWarnings("unchecked")
    public EntityInsertable<TProxy, T> insertable() {
        return getEntityQuery().insertable((T) this);
    }

    /**
     * 获取批量插入器
     *
     * @param entities 实体集合
     * @return EntityInsertable 插入器
     */
    public EntityInsertable<TProxy, T> insertable(Collection<T> entities) {
        return getEntityQuery().insertable(entities);
    }

    /**
     * 使用代理对象创建插入器
     *
     * @param tProxy 代理对象
     * @return 插入器
     */
    public EntityInsertable<TProxy, T> insertable(TProxy tProxy) {
        return getEntityQuery().insertable(tProxy);
    }

    // ==================== 更新方法 ====================

    /**
     * 获取当前实体的更新器（根据主键）
     * 用法: userEntity.updatable().executeRows()
     *
     * @return EntityUpdatable 更新器
     */
    @SuppressWarnings("unchecked")
    public EntityUpdatable<TProxy, T> updatable() {
        return getEntityQuery().updatable((T) this);
    }

    /**
     * 获取批量更新器
     *
     * @param entities 实体集合
     * @return EntityUpdatable 更新器
     */
    public EntityUpdatable<TProxy, T> updatable(Collection<T> entities) {
        return getEntityQuery().updatable(entities);
    }

    /**
     * 获取表达式更新器，可自定义更新条件和set值
     * 用法: new User().expressionUpdatable().set(u -> u.nickname(), "新昵称").where(u -> u.id().eq(1L)).executeRows()
     *
     * @return ExpressionUpdatable 表达式更新器
     */
    public ExpressionUpdatable<TProxy, T> expressionUpdatable() {
        return getEntityQuery().updatable(entityClass());
    }

    /**
     * 使用代理对象创建表达式更新器
     *
     * @param tProxy 代理对象
     * @return 表达式更新器
     */
    public ExpressionUpdatable<TProxy, T> expressionUpdatable(TProxy tProxy) {
        return getEntityQuery().expressionUpdatable(tProxy);
    }

    /**
     * 使用代理对象创建实体更新器
     *
     * @param tProxy 代理对象
     * @return 实体更新器
     */
    public EntityUpdatable<TProxy, T> entityUpdatable(TProxy tProxy) {
        return getEntityQuery().entityUpdatable(tProxy);
    }

    // ==================== 删除方法 ====================

    /**
     * 获取当前实体的删除器（根据主键）
     * 用法: userEntity.deletable().executeRows()
     *
     * @return EntityDeletable 删除器
     */
    @SuppressWarnings("unchecked")
    public EntityDeletable<TProxy, T> deletable() {
        return getEntityQuery().deletable((T) this);
    }

    /**
     * 获取批量删除器
     *
     * @param entities 实体集合
     * @return EntityDeletable 删除器
     */
    public EntityDeletable<TProxy, T> deletable(Collection<T> entities) {
        return getEntityQuery().deletable(entities);
    }

    /**
     * 获取表达式删除器，可自定义删除条件
     * 用法: new User().expressionDeletable().where(u -> u.delFlag().eq(true)).executeRows()
     *
     * @return ExpressionDeletable 表达式删除器
     */
    public ExpressionDeletable<TProxy, T> expressionDeletable() {
        return getEntityQuery().deletable(entityClass());
    }

    /**
     * 使用代理对象创建表达式删除器
     *
     * @param tProxy 代理对象
     * @return 表达式删除器
     */
    public ExpressionDeletable<TProxy, T> expressionDeletable(TProxy tProxy) {
        return getEntityQuery().expressionDeletable(tProxy);
    }

    /**
     * 使用代理对象创建实体删除器
     *
     * @param tProxy 代理对象
     * @return 实体删除器
     */
    public EntityDeletable<TProxy, T> entityDeletable(TProxy tProxy) {
        return getEntityQuery().entityDeletable(tProxy);
    }

    // ==================== 保存方法 ====================

    /**
     * 获取当前实体的保存器（存在则更新，不存在则插入）
     * 用法: userEntity.savable().executeCommand()
     *
     * @return EntitySavable 保存器
     */
    @SuppressWarnings("unchecked")
    public EntitySavable<TProxy, T> savable() {
        return getEntityQuery().savable((T) this);
    }

    /**
     * 获取批量保存器
     *
     * @param entities 实体集合
     * @return EntitySavable 保存器
     */
    public EntitySavable<TProxy, T> savable(Collection<T> entities) {
        return getEntityQuery().savable(entities);
    }

    /**
     * 使用代理对象创建保存器
     *
     * @param tProxy 代理对象
     * @return 保存器
     */
    public EntitySavable<TProxy, T> savable(TProxy tProxy) {
        return getEntityQuery().savable(tProxy);
    }

    /**
     * 创建DbSet
     *
     * @param tProxy 代理对象
     * @return DbSet
     */
    public DbSet<TProxy, T> createDbSet(TProxy tProxy) {
        return getEntityQuery().createDbSet(tProxy);
    }

    /**
     * 创建当前实体的DbSet
     *
     * @return DbSet
     */
    public DbSet<TProxy, T> createDbSet() {
        return getEntityQuery().createDbSet(EntityQueryProxyManager.create(entityClass()));
    }

    // ==================== 查询便捷方法 ====================

    /**
     * 根据主键查询单个实体
     *
     * @param id 主键
     * @return 实体对象，不存在返回 null
     */
    public T findById(Object id) {
        return getEntityQuery().queryable(entityClass())
                               .findOrNull(id);
    }

    /**
     * 根据主键查询单个实体（不存在则抛出异常）
     *
     * @param id 主键
     * @return 实体对象
     */
    public T findByIdNotNull(Object id) {
        return getEntityQuery().queryable(entityClass())
                               .findNotNull(id);
    }

    /**
     * 查询所有数据
     *
     * @return 实体列表
     */
    public List<T> findAll() {
        return getEntityQuery().queryable(entityClass())
                               .toList();
    }

    /**
     * 统计总数
     *
     * @return 记录数
     */
    public long count() {
        return getEntityQuery().queryable(entityClass())
                               .count();
    }

    /**
     * 判断是否存在数据
     *
     * @return 是否存在
     */
    public boolean exists() {
        return getEntityQuery().queryable(entityClass())
                               .any();
    }

    // ==================== 实例追踪方法 ====================

    /**
     * 添加当前实体到追踪环境
     * 如果当前线程未开启追踪那么添加直接忽略无效
     *
     * @return true:添加成功, false:已经存在相同对象或未开启追踪
     */
    public boolean addTrackingThis() {
        return getEntityQuery().addTracking(this);
    }

    /**
     * 从追踪环境移除当前实体
     *
     * @return 是否移除成功
     */
    public boolean removeTrackingThis() {
        return getEntityQuery().removeTracking(this);
    }

    /**
     * 获取当前实体的追踪状态（不存在则抛出异常）
     *
     * @return EntityState 实体状态
     */
    public EntityState trackEntityStateNotNull() {
        return getEntityQuery().getTrackEntityStateNotNull(this);
    }

    /**
     * 获取当前实体的追踪状态（可能返回null）
     *
     * @return EntityState 实体状态，可能为null
     */
    public @Nullable EntityState trackEntityState() {
        return getEntityQuery().getTrackEntityState(this);
    }

    /**
     * 对保存的对象进行主键设置
     * 如果对象的id不存在追踪上下文那么将会被视为非法id从而重新赋值
     */
    public void saveEntitySetPrimaryKey() {
        getEntityQuery().saveEntitySetPrimaryKey(this);
    }

    // ==================== 关联加载方法 ====================

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
