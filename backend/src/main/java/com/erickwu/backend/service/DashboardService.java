package com.erickwu.backend.service;

import com.erickwu.backend.vo.CategoryDataVO;
import com.erickwu.backend.vo.DashboardOverviewVO;
import com.erickwu.backend.vo.RecentActivityVO;
import com.erickwu.backend.vo.TrendDataVO;

import java.util.List;

/**
 * Dashboard数据统计服务接口
 */
public interface DashboardService {

    /**
     * 获取Dashboard概览数据
     * @param userId 用户ID
     * @return 概览数据
     */
    DashboardOverviewVO getOverview(Long userId);

    /**
     * 获取学习趋势数据
     * @param userId 用户ID
     * @param period 时间周期：7d(7天), 30d(30天), 90d(90天)
     * @return 趋势数据列表
     */
    List<TrendDataVO> getLearningTrend(Long userId, String period);

    /**
     * 获取技能分布数据
     * @param userId 用户ID
     * @return 技能分类分布
     */
    List<CategoryDataVO> getSkillDistribution(Long userId);

    /**
     * 获取技能等级分布
     * @param userId 用户ID
     * @return 技能等级分布
     */
    List<CategoryDataVO> getSkillLevelDistribution(Long userId);

    /**
     * 获取最近活动记录
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 最近活动列表
     */
    List<RecentActivityVO> getRecentActivities(Long userId, int limit);
}
