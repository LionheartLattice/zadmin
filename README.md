# ZAdmin 后台管理系统

ZAdmin 是一个基于 **Java 25** 和 **Spring Boot 3** 构建的现代化、模块化后台管理系统。项目采用 Maven 多模块架构设计，集成了高性能 ORM 框架 Easy-Query，致力于提供高效、灵活且易于维护的开发体验。

## 🚀 项目特性

-   **前沿技术栈**: 基于最新的 **Java SDK 25** 开发，拥抱 Java 生态最新特性。
-   **模块化架构**: 采用清晰的 Maven 多模块设计 (`core`, `entity`, `user_center` 等)，实现关注点分离，加快构建速度。
-   **高性能 ORM**: 集成 **[Easy-Query](https://xuejm.gitee.io/easy-query-doc/)**，支持强类型 Lambda 查询、多数据源动态切换、自动化主键生成等高级特性。
-   **动态数据源**: 核心模块内置动态数据源支持，轻松应对多数据库场景（MySQL/PostgreSQL 等），支持运行时动态注册 Bean。
-   **开发效率**: 集成 Lombok、Knife4j (OpenAPI 3)、Hutool 等工具库，大幅减少样板代码，提升开发效率。
-   **数据库支持**: 默认适配 MySQL 与 PostgreSQL，提供标准 DDL 脚本。

## 📂 模块结构

```text
zadmin
├── a_start       # [启动模块] 应用程序入口，聚合各业务模块
├── core          # [核心模块] 公共配置、工具类、动态数据源、反射加载器
├── entity        # [实体模块] POJO、DTO、VO 及数据库映射实体 (无业务逻辑)
├── generator     # [代码生成] 代码生成器模块 (可选)
└── user_center   # [业务模块] 用户中心业务逻辑 (Controller, Service 等)
```

## 🛠️ 技术选型

| 技术 | 说明 | 版本 |
| :--- | :--- | :--- |
| **Java** | 编程语言 | **25** |
| **Spring Boot** | 核心框架 | 3.x / 4.x (Preview) |
| **Easy-Query** | ORM 框架 | 3.1.57 |
| **Knife4j** | API 文档 | 4.5.0 |
| **Hutool** | 工具类库 | 5.8.41 |
| **PostgreSQL / MySQL** | 数据库 | 适配多库 |

## ⚡ 快速开始

### 1. 环境准备
-   JDK 25
-   Maven 3.8+
-   PostgreSQL 或 MySQL 数据库

### 2. 数据库初始化
执行项目根目录下的 SQL 脚本以初始化数据库表结构及测试数据：
-   文件路径: `test_data.sql` (需手动查找或向开发者索取，示例如下)
-   默认创建 `z_user` 表及 10 条测试数据。

```sql
-- 示例：创建用户表
CREATE TABLE z_user (
    id BIGINT NOT NULL PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    -- ...
);
```

### 3. 配置修改
在 `a_start` 模块的 `application.yml` (或 `properties`) 中配置您的数据库连接信息。

### 4. 编译与运行
由于使用了 Java 25 预览特性或最新版 Spring Boot，建议使用以下命令构建：

```bash
mvn clean install -DskipTests
```

启动 `a_start` 模块下的主类即可运行系统。

### 5. 访问文档
启动成功后，访问 Knife4j 接口文档：
`http://localhost:8080/doc.html`

## 💡 核心亮点：解耦设计

为了加快构建速度并保持模块纯净，本项目采用了独特的解耦策略。例如，`core` 模块通过 **反射 (Reflection)** 机制动态加载 `entity` 模块中的组件（如 `SnowflakePrimaryKeyGenerator`），从而避免了 `core` 对 `entity` 的循环依赖或编译期强依赖。

```java
// Core 模块动态加载主键生成器示例
Object generator = ReflectUtil.newInstance("po.user_center.io.github.lionheartlattice.SnowflakePrimaryKeyGenerator");
if (generator instanceof PrimaryKeyGenerator) {
    queryConfiguration.applyPrimaryKeyGenerator((PrimaryKeyGenerator) generator);
}
```

## 📄 版权说明

Copyright © 2024 LionHeart. All rights reserved.
