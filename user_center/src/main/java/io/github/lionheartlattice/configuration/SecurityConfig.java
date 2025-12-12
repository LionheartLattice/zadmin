package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.user_center.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // 禁用CSRF
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态会话
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/z_login/login")
                    .permitAll() // 允许登录接口匿名访问
                    .anyRequest()
                    .authenticated() // 其他请求需要认证
            )
            .addFilterBefore(new TokenAuthenticationFilter(loginService), UsernamePasswordAuthenticationFilter.class); // 添加token过滤器

        return http.build();
    }
}
