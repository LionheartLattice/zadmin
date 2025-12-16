# Jackson 视图使用说明

## 概述

使用 Jackson 的 `@JsonView` 注解来统一管理 DTO 的创建和更新操作，避免创建重复的 CreateDTO 和 UpdateDTO。

## 视图定义

在 `Views.java` 中定义了两种视图：

```java
public class Views {
    // 创建视图：不包含 ID 字段
    public interface Create {}
    
    // 更新视图：包含 ID 字段（继承 Create）
    public interface Update extends Create {}
}
```

**重要说明：** `Update` 继承 `Create`，所以：
- 标记为 `@JsonView(Views.Create.class)` 的字段在 Create 和 Update 视图中都会被序列化
- 标记为 `@JsonView(Views.Update.class)` 的字段只在 Update 视图中被序列化

## DTO 编写规范

### 核心原则

**只在主键字段上添加 `@JsonView(Views.Update.class)` 注解，其他字段不需要添加任何 `@JsonView` 注解。**

Jackson 的默认行为：**没有 `@JsonView` 注解的字段在所有视图中都会被序列化。**

### 1. 主键字段

主键字段添加 `@JsonView(Views.Update.class)` 注解，表示只在 Update 视图中可见：

```java
@JsonView(Views.Update.class)
@Schema(description = "租户ID", requiredMode = Schema.RequiredMode.REQUIRED)
@Column(value = "id")
@NotNull(groups = Update.class)
private BigDecimal id;
```

### 2. 普通字段

普通字段**不需要**添加 `@JsonView` 注解，默认在所有视图中都可见：

```java
@Schema(description = "租户名称", requiredMode = Schema.RequiredMode.REQUIRED)
@NotNull(groups = {Create.class, Update.class})
private String name;
```

### 3. 完整示例

```java
@Data
@Schema(description = "租户信息")
public class TenantDTO {

    // 主键：只在 Update 视图中可见
    @JsonView(Views.Update.class)
    @NotNull(groups = Update.class)
    private BigDecimal id;

    // 普通字段：不加 @JsonView 注解，在所有视图中都可见
    @NotNull(groups = {Create.class, Update.class})
    private String name;

    private String remark;

    // 验证分组
    public interface Create {}
    public interface Update {}
}
```

## Controller 使用方式

### 1. 创建接口（不含 id）

```java
@PostMapping("/create")
public ApiResult<Boolean> create(
    @Validated(TenantDTO.Create.class) 
    @JsonView(Views.Create.class) 
    @RequestBody TenantDTO dto) {
    return ApiResult.success(service.create(dto));
}
```

### 2. 更新接口（包含 id）

```java
@PostMapping("/update")
public ApiResult<Boolean> update(
    @Validated(TenantDTO.Update.class) 
    @JsonView(Views.Update.class) 
    @RequestBody TenantDTO dto) {
    return ApiResult.success(service.update(dto));
}
```

### 3. 查询接口（返回包含 id）

```java
@PostMapping("/getbyid")
@JsonView(Views.Update.class)
public ApiResult<TenantDTO> getById(@RequestParam BigDecimal id) {
    return ApiResult.success(service.getById(id));
}
```

## 工作原理

### Create 视图（创建）
```json
// 请求体不包含 id
{
  "name": "测试租户",
  "contactName": "张三",
  "contactPhone": "13800138000"
}
```

### Update 视图（更新）
```json
// 请求体包含 id
{
  "id": 123456,
  "name": "测试租户",
  "contactName": "张三",
  "contactPhone": "13800138000"
}
```

## 优势

1. **减少代码冗余**：只需一个 DTO 类，不需要分别创建 CreateDTO 和 UpdateDTO
2. **维护简单**：字段变更只需修改一处
3. **类型安全**：编译时检查，避免运行时错误
4. **灵活性强**：可以轻松扩展更多视图（如 Response、Detail 等）

## 注意事项

1. 必须在 Controller 方法上添加 `@JsonView` 注解来指定使用哪个视图
2. `@Validated` 的分组验证与 `@JsonView` 配合使用，确保数据完整性
3. Update 视图继承 Create 视图，所以 Update 操作可以访问所有字段
4. Service 层不需要关心视图，正常使用 DTO 即可

## 迁移指南

如果要将现有的 CreateDTO 和 UpdateDTO 合并为一个 DTO：

1. 创建新的统一 DTO
2. **只在主键字段上**添加 `@JsonView(Views.Update.class)`
3. 其他字段**不需要**添加 `@JsonView` 注解
4. 更新 Controller 的方法签名和注解
5. 更新 Service 的方法签名
6. 删除旧的 CreateDTO 和 UpdateDTO

## 示例项目结构

```
dto/
├── Views.java              // 视图定义
├── TenantDTO.java          // 统一的 DTO
├── TenantCreateDTO.java    // （可删除）
└── TenantUpdateDTO.java    // （可删除）
```

