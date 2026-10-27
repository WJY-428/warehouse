package com.warehouse.warehouse_management.config;

import com.warehouse.warehouse_management.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置：注册登录拦截器白名单 + 跨域规则。
 * 注意：拦截器的路径是相对于 context-path(/api) 的。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 只放行登录接口；注册不对外开放，账号由管理员在库中维护（默认账号见 DataInitializer）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/error",
                        "/favicon.ico"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 前端如果不用 Vite 代理、直接跨域访问 8081，这里放行本机来源
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
