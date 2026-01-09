package com.erickwu.backend.interceptor;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.model.ErrorCode;
import com.erickwu.backend.util.JwtUtil;
import com.erickwu.backend.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 * 用于验证请求中的 JWT Token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 获取 Authorization 请求头
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        // 提取 Token
        String token = authHeader.substring(7);

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.USER_TOKEN_INVALID, "Token 无效或已过期");
        }

        // 从 Token 中获取用户信息并存入 UserContext
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        UserContext.setCurrentUserId(userId);
        UserContext.setCurrentUsername(username);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        // 清除 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
