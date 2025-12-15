package io.github.lionheartlattice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 用于配置跨域资源共享 (CORS) 以解决前端开发环境的跨域问题
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有路径应用跨域配置
        registry.addMapping("/**")
                // 允许所有来源 (使用 Pattern 配合 allowCredentials)
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证 (如 Cookie)
                .allowCredentials(true)
                // 预检请求的缓存时间 (秒)
                .maxAge(3600);
    }
}
