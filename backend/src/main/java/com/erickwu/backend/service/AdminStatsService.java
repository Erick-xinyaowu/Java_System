package com.erickwu.backend.service;

import com.erickwu.backend.vo.AdminStatsVO;

/**
 * 管理员统计服务接口
 */
public interface AdminStatsService {
    
    /**
     * 获取管理员控制台统计数据
     */
    AdminStatsVO getStats();
    
    /**
     * 检查用户是否是管理员
     */
    boolean isAdmin(Long userId);
}
