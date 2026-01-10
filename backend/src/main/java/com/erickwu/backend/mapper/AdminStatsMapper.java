package com.erickwu.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计 Mapper
 */
@Mapper
public interface AdminStatsMapper {

    /**
     * 获取总用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    Long countTotalUsers();

    /**
     * 获取总简历数
     */
    @Select("SELECT COUNT(*) FROM resume")
    Long countTotalResumes();

    /**
     * 获取24小时内活跃用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM chat_session WHERE updated_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)")
    Long countActiveUsers();

    /**
     * 获取今日新增用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURDATE()")
    Long countTodayNewUsers();

    /**
     * 获取今日新增简历数
     */
    @Select("SELECT COUNT(*) FROM resume WHERE DATE(created_at) = CURDATE()")
    Long countTodayNewResumes();

    /**
     * 获取已处理简历数（有分析报告的）
     */
    @Select("SELECT COUNT(*) FROM resume_version WHERE analysis_report IS NOT NULL")
    Long countProcessedResumes();

    /**
     * 获取待处理简历数（无分析报告的）
     */
    @Select("SELECT COUNT(*) FROM resume_version WHERE analysis_report IS NULL")
    Long countPendingResumes();

    /**
     * 获取最近7天每日用户注册数
     */
    @Select("""
        SELECT DATE(created_at) as date, COUNT(*) as count 
        FROM users 
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) 
        GROUP BY DATE(created_at) 
        ORDER BY date ASC
        """)
    List<Map<String, Object>> getUserGrowthLast7Days();

    /**
     * 获取24小时活跃度（每小时活跃会话数）
     */
    @Select("""
        SELECT HOUR(updated_at) as hour, COUNT(*) as count 
        FROM chat_session 
        WHERE updated_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) 
        GROUP BY HOUR(updated_at) 
        ORDER BY hour ASC
        """)
    List<Map<String, Object>> getHourlyActivity();

    /**
     * 获取热门技能统计（从简历技能字段）
     */
    @Select("""
        SELECT skill_name, COUNT(*) as count 
        FROM resume_skill 
        GROUP BY skill_name 
        ORDER BY count DESC 
        LIMIT 6
        """)
    List<Map<String, Object>> getTopSkills();

    /**
     * 获取专业分布统计
     */
    @Select("""
        SELECT major, COUNT(*) as count 
        FROM resume 
        WHERE major IS NOT NULL AND major != '' 
        GROUP BY major 
        ORDER BY count DESC 
        LIMIT 5
        """)
    List<Map<String, Object>> getMajorDistribution();

    /**
     * 获取今日分析次数
     */
    @Select("SELECT COUNT(*) FROM resume_version WHERE DATE(created_at) = CURDATE()")
    Long countTodayAnalysis();

    /**
     * 检查表是否存在
     */
    @Select("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = #{tableName}")
    int tableExists(@Param("tableName") String tableName);
}
