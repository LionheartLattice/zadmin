package io.github.lionheartlattice.util.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 缓存请求体，便于异常时记录
 */
@Component
public class BodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 设置缓存上限（字节），可根据实际需要调整
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request, 4 * 1024 * 1024);
        filterChain.doFilter(wrapped, response);
    }
}
