package com.erickwu.backend.controller;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.service.AdminStatsService;
import com.erickwu.backend.util.JwtUtil;
import com.erickwu.backend.vo.AdminStatsVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制台 Controller
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminStatsService adminStatsService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminStatsService adminStatsService, JwtUtil jwtUtil) {
        this.adminStatsService = adminStatsService;
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
