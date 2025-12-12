package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token 鉴权过滤器：从 HTTP Header 解析 token，并转换为 Spring Security 的 Authentication
 * <p>
 * 支持两种 Header：
 * 1) Authorization: Bearer <token>（标准）
 * 2) X-Token: <token>（自定义）
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        // 如果能解析出 token，且当前还没有认证
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext()
                                                               .getAuthentication() == null) {
            User user = loginService.getUserByToken(token);
            if (user != null) {
                // 构建认证对象（principal = user 对象，credentials = token）
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, token, AuthorityUtils.NO_AUTHORITIES  // 暂时没有权限，后续可补
                );
                SecurityContextHolder.getContext()
                                     .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP Header 中解析 token
     * <p>
     * 优先级：
     * 1) Authorization: Bearer <token>
     * 2) X-Token: <token>
     */
    private String resolveToken(HttpServletRequest request) {
        // 1) 标准 Authorization header
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring("Bearer ".length())
                       .trim();
        }

        // 2) 自定义 X-Token header
        String xToken = request.getHeader("X-Token");
        if (StringUtils.hasText(xToken)) {
            return xToken.trim();
        }

        return null;
    }
}
