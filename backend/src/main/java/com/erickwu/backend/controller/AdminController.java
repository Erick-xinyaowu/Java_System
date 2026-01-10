package com.erickwu.backend.controller;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.dto.UserManageDTO;
import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.service.AdminStatsService;
import com.erickwu.backend.service.UserManageService;
import com.erickwu.backend.util.JwtUtil;
import com.erickwu.backend.vo.AdminStatsVO;
import com.erickwu.backend.vo.UserManageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制台 Controller
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminStatsService adminStatsService;
    private final UserManageService userManageService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminStatsService adminStatsService, 
                          UserManageService userManageService,
                          JwtUtil jwtUtil) {
        this.adminStatsService = adminStatsService;
        this.userManageService = userManageService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取管理员控制台统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<AdminStatsVO> getStats(HttpServletRequest request) {
        Long userId = getUserId(request);
        
        // 验证管理员权限
        if (!adminStatsService.isAdmin(userId)) {
            throw new BusinessException(403, "无权访问管理员控制台");
        }
        
        AdminStatsVO stats = adminStatsService.getStats();
        return ApiResponse.success(stats);
    }

    /**
     * 检查当前用户是否是管理员
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> checkAdmin(HttpServletRequest request) {
        Long userId = getUserId(request);
        boolean isAdmin = adminStatsService.isAdmin(userId);
        return ApiResponse.success(isAdmin);
    }

    // ==================== 用户管理 API ====================

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public ApiResponse<List<UserManageVO>> getAllUsers(HttpServletRequest request) {
        checkAdminPermission(request);
        List<UserManageVO> users = userManageService.getAllUsers();
        return ApiResponse.success(users);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/users/search")
    public ApiResponse<List<UserManageVO>> searchUsers(
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        checkAdminPermission(request);
        List<UserManageVO> users = userManageService.searchUsers(keyword);
        return ApiResponse.success(users);
    }

    /**
     * 获取单个用户
     */
    @GetMapping("/users/{id}")
    public ApiResponse<UserManageVO> getUserById(
            @PathVariable Long id,
            HttpServletRequest request) {
        checkAdminPermission(request);
        UserManageVO user = userManageService.getUserById(id);
        return ApiResponse.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping("/users")
    public ApiResponse<UserManageVO> createUser(
            @RequestBody UserManageDTO dto,
            HttpServletRequest request) {
        checkAdminPermission(request);
        UserManageVO user = userManageService.createUser(dto);
        return ApiResponse.success(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/users/{id}")
    public ApiResponse<UserManageVO> updateUser(
            @PathVariable Long id,
            @RequestBody UserManageDTO dto,
            HttpServletRequest request) {
        checkAdminPermission(request);
        dto.setId(id);
        UserManageVO user = userManageService.updateUser(dto);
        return ApiResponse.success(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        checkAdminPermission(request);
        userManageService.deleteUser(id);
        return ApiResponse.success(null);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request) {
        checkAdminPermission(request);
        Integer status = body.get("status");
        userManageService.updateUserStatus(id, status);
        return ApiResponse.success(null);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/users/{id}/password")
    public ApiResponse<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        checkAdminPermission(request);
        String newPassword = body.get("password");
        userManageService.resetPassword(id, newPassword);
        return ApiResponse.success(null);
    }

    /**
     * 检查管理员权限
     */
    private void checkAdminPermission(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (!adminStatsService.isAdmin(userId)) {
            throw new BusinessException(403, "无权访问管理员功能");
        }
    }

    /**
     * 从请求中获取用户 ID
     */
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
