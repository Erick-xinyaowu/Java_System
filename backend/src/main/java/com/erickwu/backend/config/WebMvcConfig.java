package com.erickwu.backend.config;

import com.erickwu.backend.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置拦截器路径等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @NonNull
    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(@NonNull JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有 /api/** 请求
                .addPathPatterns("/api/**")
                // 排除不需要登录的接口
                .excludePathPatterns(
                        "/api/user/login",           // 登录
                        "/api/user/register",        // 注册
                        "/api/user/check-username",  // 检查用户名
                        "/api/user/check-email",     // 检查邮箱
                        "/api/health/**"             // 健康检查
                );
    }
}
