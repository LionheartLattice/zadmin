package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.user_center.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;

    @Value("${app.auth.enabled:true}")
    private boolean authEnabled;

    @Value("${app.auth.token-key-prefix:token:}")
    private String tokenKeyPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        TokenAuthenticationFilter tokenFilter = new TokenAuthenticationFilter(loginService, tokenKeyPrefix);

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, resp, e) -> {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
                resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                resp.getWriter()
                    .write("{\"success\":false,\"message\":\"未登录或token无效\"}");
            }));

        // 根据配置决定是否启用安全校验
        if (authEnabled) {
            http.authorizeHttpRequests(auth -> auth.requestMatchers("/z_login/login")
                                                   .permitAll()
                                                   .requestMatchers("/doc.html", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.json", "/webjars/**")
                                                   .permitAll()
                                                   .anyRequest()
                                                   .authenticated())
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            // 开发环境:全部放开
            http.authorizeHttpRequests(auth -> auth.anyRequest()
                                                   .permitAll());
        }

        return http.build();
    }
}
