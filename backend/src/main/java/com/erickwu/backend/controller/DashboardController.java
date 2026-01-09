package com.erickwu.backend.controller;

import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.service.DashboardService;
import com.erickwu.backend.util.UserContext;
import com.erickwu.backend.vo.CategoryDataVO;
import com.erickwu.backend.vo.DashboardOverviewVO;
import com.erickwu.backend.vo.RecentActivityVO;
import com.erickwu.backend.vo.TrendDataVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard数据统计控制器
 * 提供前端Dashboard所需的各类统计数据
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取Dashboard概览数据
     * 用于展示统计卡片
     */
    @GetMapping("/overview")
    public ApiResponse<DashboardOverviewVO> getOverview() {
        Long userId = UserContext.getCurrentUserId();
        DashboardOverviewVO overview = dashboardService.getOverview(userId);
        return ApiResponse.success(overview);
    }

    /**
     * 获取学习趋势数据
     * 用于折线图展示
     * @param period 时间周期：7d(默认), 30d, 90d
     */
    @GetMapping("/trend")
    public ApiResponse<List<TrendDataVO>> getLearningTrend(
            @RequestParam(defaultValue = "7d") String period) {
        Long userId = UserContext.getCurrentUserId();
        List<TrendDataVO> trendData = dashboardService.getLearningTrend(userId, period);
        return ApiResponse.success(trendData);
    }

    /**
     * 获取技能分布数据
     * 用于饼图展示技能分类分布
     */
    @GetMapping("/skill-distribution")
    public ApiResponse<List<CategoryDataVO>> getSkillDistribution() {
        Long userId = UserContext.getCurrentUserId();
        List<CategoryDataVO> distribution = dashboardService.getSkillDistribution(userId);
        return ApiResponse.success(distribution);
    }

    /**
     * 获取技能等级分布
     * 用于柱状图展示技能等级分布
     */
    @GetMapping("/skill-level")
    public ApiResponse<List<CategoryDataVO>> getSkillLevelDistribution() {
        Long userId = UserContext.getCurrentUserId();
        List<CategoryDataVO> distribution = dashboardService.getSkillLevelDistribution(userId);
        return ApiResponse.success(distribution);
    }

    /**
     * 获取综合分布数据
     * 同时返回技能分类和技能等级分布
     */
    @GetMapping("/distribution")
    public ApiResponse<Map<String, List<CategoryDataVO>>> getDistribution() {
        Long userId = UserContext.getCurrentUserId();
        
        Map<String, List<CategoryDataVO>> result = new HashMap<>();
        result.put("skillCategory", dashboardService.getSkillDistribution(userId));
        result.put("skillLevel", dashboardService.getSkillLevelDistribution(userId));
        
        return ApiResponse.success(result);
    }

    /**
     * 获取最近活动记录
     * @param limit 返回数量，默认10条
     */
    @GetMapping("/activities")
    public ApiResponse<List<RecentActivityVO>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = UserContext.getCurrentUserId();
        List<RecentActivityVO> activities = dashboardService.getRecentActivities(userId, limit);
        return ApiResponse.success(activities);
    }

    /**
     * 获取Dashboard完整数据
     * 一次性返回所有Dashboard需要的数据
     */
    @GetMapping("/all")
    public ApiResponse<Map<String, Object>> getAllDashboardData(
            @RequestParam(defaultValue = "7d") String period) {
        Long userId = UserContext.getCurrentUserId();
        
        Map<String, Object> data = new HashMap<>();
        data.put("overview", dashboardService.getOverview(userId));
        data.put("trend", dashboardService.getLearningTrend(userId, period));
        data.put("skillDistribution", dashboardService.getSkillDistribution(userId));
        data.put("skillLevel", dashboardService.getSkillLevelDistribution(userId));
        data.put("activities", dashboardService.getRecentActivities(userId, 10));
        
        return ApiResponse.success(data);
    }
}
