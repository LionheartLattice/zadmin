package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.user_center.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token 鉴权过滤器
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;
    /**
     * 与 LoginService 写 Redis 的前缀保持一致，来自配置
     */
    private final String tokenKeyPrefix;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext()
                                                               .getAuthentication() == null) {
            User user = loginService.getUserByToken(token);
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, token, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext()
                                     .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 解析请求头中的 token
     */
    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth)) {
            // 自定义前缀
            if (StringUtils.hasText(tokenKeyPrefix) && auth.startsWith(tokenKeyPrefix)) {
                return auth.substring(tokenKeyPrefix.length())
                           .trim();
            }
        }

        return auth;
    }
}
