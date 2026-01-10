package com.erickwu.backend.vo;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计数据 VO
 */
public class AdminStatsVO {
    
    // 基础统计
    private Long totalUsers;
    private Long totalResumes;
    private Long activeUsers;  // 24小时内活跃用户
    private Double systemHealth;  // 系统健康度百分比
    
    // 用户增长趋势（最近7天）
    private List<String> userGrowthDates;
    private List<Long> userGrowthCounts;
    
    // 简历处理状态
    private Long processedResumes;
    private Long pendingResumes;
    
    // 热门技能统计
    private List<String> topSkillNames;
    private List<Long> topSkillCounts;
    
    // 24小时活跃度
    private List<String> hourlyLabels;
    private List<Long> hourlyCounts;
    
    // 专业分布
    private List<String> majorNames;
    private List<Long> majorCounts;
    
    // 系统资源
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Double networkUsage;
    
    // 今日统计
    private Long todayNewUsers;
    private Long todayNewResumes;
    private Long todayAnalysis;
    
    // Getters and Setters
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
    
    public Long getTotalResumes() { return totalResumes; }
    public void setTotalResumes(Long totalResumes) { this.totalResumes = totalResumes; }
    
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    
    public Double getSystemHealth() { return systemHealth; }
    public void setSystemHealth(Double systemHealth) { this.systemHealth = systemHealth; }
    
    public List<String> getUserGrowthDates() { return userGrowthDates; }
    public void setUserGrowthDates(List<String> userGrowthDates) { this.userGrowthDates = userGrowthDates; }
    
    public List<Long> getUserGrowthCounts() { return userGrowthCounts; }
    public void setUserGrowthCounts(List<Long> userGrowthCounts) { this.userGrowthCounts = userGrowthCounts; }
    
    public Long getProcessedResumes() { return processedResumes; }
    public void setProcessedResumes(Long processedResumes) { this.processedResumes = processedResumes; }
    
    public Long getPendingResumes() { return pendingResumes; }
    public void setPendingResumes(Long pendingResumes) { this.pendingResumes = pendingResumes; }
    
    public List<String> getTopSkillNames() { return topSkillNames; }
    public void setTopSkillNames(List<String> topSkillNames) { this.topSkillNames = topSkillNames; }
    
    public List<Long> getTopSkillCounts() { return topSkillCounts; }
    public void setTopSkillCounts(List<Long> topSkillCounts) { this.topSkillCounts = topSkillCounts; }
    
    public List<String> getHourlyLabels() { return hourlyLabels; }
    public void setHourlyLabels(List<String> hourlyLabels) { this.hourlyLabels = hourlyLabels; }
    
    public List<Long> getHourlyCounts() { return hourlyCounts; }
    public void setHourlyCounts(List<Long> hourlyCounts) { this.hourlyCounts = hourlyCounts; }
    
    public List<String> getMajorNames() { return majorNames; }
    public void setMajorNames(List<String> majorNames) { this.majorNames = majorNames; }
    
    public List<Long> getMajorCounts() { return majorCounts; }
    public void setMajorCounts(List<Long> majorCounts) { this.majorCounts = majorCounts; }
    
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public Double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(Double diskUsage) { this.diskUsage = diskUsage; }
    
    public Double getNetworkUsage() { return networkUsage; }
    public void setNetworkUsage(Double networkUsage) { this.networkUsage = networkUsage; }
    
    public Long getTodayNewUsers() { return todayNewUsers; }
    public void setTodayNewUsers(Long todayNewUsers) { this.todayNewUsers = todayNewUsers; }
    
    public Long getTodayNewResumes() { return todayNewResumes; }
    public void setTodayNewResumes(Long todayNewResumes) { this.todayNewResumes = todayNewResumes; }
    
    public Long getTodayAnalysis() { return todayAnalysis; }
    public void setTodayAnalysis(Long todayAnalysis) { this.todayAnalysis = todayAnalysis; }
}
