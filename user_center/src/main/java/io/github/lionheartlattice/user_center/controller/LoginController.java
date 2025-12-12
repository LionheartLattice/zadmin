package io.github.lionheartlattice.user_center.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录管理")
@RestController
@RequestMapping("z_login")
@RequiredArgsConstructor
public class LoginController {

    @PostMapping("/login")
    public String login(){
        return "登录成功";
    }
}
