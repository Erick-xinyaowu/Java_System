package com.erickwu.backend.controller;

import com.erickwu.backend.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
public class HealthController {

    /**
     * Ping 端点 - 用于检查服务是否运行
     */
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().toString());
        healthInfo.put("service", "Career Planner Backend");
        healthInfo.put("version", "1.0.0");
        return ApiResponse.success(healthInfo);
    }

    /**
     * API 信息端点
     */
    @GetMapping("/api/info")
    public ApiResponse<Map<String, Object>> apiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Career Planner API");
        info.put("version", "1.0.0");
        info.put("description", "智能职业规划与学业分析系统 API");
        info.put("endpoints", new String[]{
            "/api/user - 用户管理",
            "/api/resume - 简历管理",
            "/api/career-assessment - 职业测评",
            "/api/career-report - 职业报告",
            "/api/study-activity - 学习活动"
        });
        return ApiResponse.success(info);
    }
}
