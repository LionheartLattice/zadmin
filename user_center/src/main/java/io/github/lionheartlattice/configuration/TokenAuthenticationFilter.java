package io.github.lionheartlattice.configuration;

import io.github.lionheartlattice.entity.user_center.po.Menu;
import io.github.lionheartlattice.entity.user_center.vo.UserWithMenu;
import io.github.lionheartlattice.login.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext()
                                                               .getAuthentication() == null) {
            UserWithMenu userWithMenu = loginService.getUserByToken(token);
            if (userWithMenu != null) {
                // 1. 初始化权限列表
                List<GrantedAuthority> authorities = new ArrayList<>();

                // 2. 从菜单列表中提取 permissions 字段并转换为 GrantedAuthority
                if (userWithMenu.getMenuList() != null) {
                    authorities = userWithMenu.getMenuList()
                                              .stream()
                                              .map(Menu::getPermissions)       // 获取权限标识字符串
                                              .filter(StringUtils::hasText)    // 过滤掉空值或空字符串
                                              .map(SimpleGrantedAuthority::new)// 转换为 Spring Security 权限对象
                                              .collect(Collectors.toList());
                }

                // 3. 构建 Authentication 对象并注入权限
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userWithMenu, token, authorities);

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
