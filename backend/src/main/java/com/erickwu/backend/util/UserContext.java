package com.erickwu.backend.util;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setCurrentUsername(String username) {
        currentUsername.set(username);
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    /**
     * 清除当前线程的用户信息
     * 在请求结束后调用，防止内存泄漏
     */
    public static void clear() {
        currentUserId.remove();
        currentUsername.remove();
    }
}
