## 项目规范

- 使用 Java 25
- 基于 Spring Boot 4.0.0
- 使用 Maven 多模块项目结构
- 编写代码要注重简洁高效

## 代码风格

- 使用 Lombok 简化代码（@Data、@Builder、@Accessors(chain = true)）
- 类、方法、字段必须添加中文注释

## 实体类规范

- 使用 EasyQuery 框架，实体类添加 @EntityProxy 和 @Table 注解
- 主键使用 @Column(primaryKey = true, primaryKeyGenerator = SnowflakePrimaryKeyGenerator.class)
- 添加 @EasyAssertMessage 用于断言提示
- 实体类继承 BaseEntity 并实现 ProxyEntityAvailable 接口
- 使用 @Schema 注解添加 Swagger 文档描述
- 日期字段使用 LocalDate 或 Date 类型

## API 规范

- 使用 Knife4j + OpenAPI 3 生成接口文档
- Controller 使用 @RestController
- 使用 @Operation 和 @Schema 注解描述接口

## 工具类
- 优先使用 封装好的 工具类以复用
- 优先使用 Spring自带的 工具类
- 优先使用 Hutool 工具类
- C:\soft\apache-maven-3.9.9\repo 下可以看到第三方jar包和文档

## 模块依赖

- base: 基础配置和第三方依赖
- core: 核心业务逻辑
- entity: 实体类定义
- user_center: 用户中心模块
- generator: 代码生成器
- a_start: 启动模块
