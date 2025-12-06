package com.lionheart.zadmin.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello测试模块", description = "用于测试多数据源和EasyQuery集成的接口")
@RestController
@RequestMapping("hello-world")
@RequiredArgsConstructor
public class HelloController {
    @Operation(summary = "Hello World", description = "返回简单的字符串")
    @GetMapping
    public String helloWorld() {
        return "hello world!";
    }
}
