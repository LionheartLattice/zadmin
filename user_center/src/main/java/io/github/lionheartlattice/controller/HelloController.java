package io.github.lionheartlattice.controller;

import io.github.lionheartlattice.user_center.po.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static io.github.lionheartlattice.configuration.easyquery.DataAccessUtils.*;

@Tag(name = "Hello测试模块", description = "用于测试多数据源和EasyQuery集成的接口")
@RestController
@RequestMapping("hello-world-core")
@RequiredArgsConstructor
public class HelloController {
    @Operation(summary = "Hello World", description = "返回简单的字符串")
    @GetMapping
    public String helloWorld() {
        return "hello world!";
    }

    // 使用主数据源查询
    @Operation(summary = "获取所有用户(主库-JDBC)", description = "使用JdbcTemplate查询主库z_user表")
    @GetMapping("/user")
    public List<Map<String, Object>> getUser() {
        return getJdbcTemplate().queryForList("select * from z_user");
    }

    // 使用主数据源 EasyQuery 查询
    @Operation(summary = "查询小米公司(主库-EQ)", description = "使用EasyQuery查询主库")
    @GetMapping("/say")
    public List<User> say() {
        return getEntityQuery().queryable(User.class).where(c -> c.nickname()
                .like("管理")).toList();
    }

    // 使用 ds2 数据源 JdbcTemplate 查询
    @Operation(summary = "获取所有用户(DS2-JDBC)", description = "使用JdbcTemplate查询DS2库")
    @GetMapping("/ds2/user")
    public List<Map<String, Object>> getDs2User() {
        return getDs2JdbcTemplate().queryForList("select * from z_user");
    }

    // 使用 ds2 数据源 EasyQuery 查询
    @Operation(summary = "查询小米公司(DS2-EQ)", description = "使用EasyQuery查询DS2库")
    // 使用 ds2 数据源 EasyQuery 查询
    @GetMapping("/ds2/say")
    public List<User> ds2Say() {
        return getDs2EntityQuery().queryable(User.class).where(c -> c.nickname()
                .like("张")).toList();
    }
}
