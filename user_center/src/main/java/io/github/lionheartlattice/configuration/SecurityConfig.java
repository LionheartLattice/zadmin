package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.user_center.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 配置：基于自定义 Token 的无状态鉴权
 * <p>
 * 说明：
 * 1) 不使用默认的 UserDetailsService（避免自动生成密码）
 * 2) 用自定义 TokenAuthenticationFilter 直接从 Redis 校验 token
 * 3) 无状态，不使用 session
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        TokenAuthenticationFilter tokenFilter = new TokenAuthenticationFilter(loginService);

        return http.csrf(AbstractHttpConfigurer::disable)
                   .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .exceptionHandling(ex -> ex.authenticationEntryPoint((req, resp, e) -> {
                       resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                       resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
                       resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                       resp.getWriter()
                           .write("{\"success\":false,\"message\":\"未登录或token无效\"}");
                   }))
                   .authorizeHttpRequests(auth -> auth
                           // 登录 / 登出放行
                           .requestMatchers("/z_login/login", "/z_login/logout")
                           .permitAll()

                           // Swagger / Knife4j 放行
                           .requestMatchers("/doc.html", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.json", "/webjars/**")
                           .permitAll()

                           // 其他接口都需要认证
                           .anyRequest()
                           .authenticated())
                   .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                   .build();
    }
}
