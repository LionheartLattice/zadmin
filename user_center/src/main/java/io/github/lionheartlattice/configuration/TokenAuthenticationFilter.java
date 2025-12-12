package io.github.lionheartlattice.configuration;

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
 * Token 鉴权过滤器：把外部传入 token 转换为 Spring Security 的 Authentication
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Object userObj = loginService.getUserByToken(token);
            if (userObj != null) {
                // authorities 暂时给空；后续你要做角色/权限再补
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userObj, token, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1) 优先标准 Authorization: Bearer <token>
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring("Bearer ".length()).trim();
        }
        // 2) 兼容自定义头：X-Token: <token>
        String xToken = request.getHeader("X-Token");
        return StringUtils.hasText(xToken) ? xToken.trim() : null;
    }
}
