package io.github.lionheartlattice.configuration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 缓存请求体，便于异常时记录
 */
@Component
public class BodyCachingFilter extends OncePerRequestFilter {

    @Value("${app.request-cache.max-size:100MB}")
    private DataSize cacheLimit;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        int limitBytes = (int) Math.min(cacheLimit.toBytes(), Integer.MAX_VALUE);
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request, limitBytes);
        filterChain.doFilter(wrapped, response);
    }
}
