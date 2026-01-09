package com.erickwu.backend.vo;

import java.time.LocalDateTime;

/**
 * Dashboard概览数据VO
 * 用于展示统计卡片数据
 */
public class DashboardOverviewVO {
    
    /** 简历完成度（百分比） */
    private Integer resumeCompleteness;
    
    /** 技能总数 */
    private Integer totalSkills;
    
    /** 精通技能数 */
    private Integer expertSkills;
    
    /** 教育经历数 */
    private Integer educationCount;
    
    /** 工作经历数 */
    private Integer workExperienceCount;
    
    /** 测评完成数 */
    private Integer assessmentCount;
    
    /** 学习记录数 */
    private Integer learningRecordCount;
    
    /** 总学习时长（小时） */
    private Double totalLearningHours;
    
    /** 本周学习时长（小时） */
    private Double weeklyLearningHours;
    
    /** 报告数量 */
    private Integer reportCount;
    
    /** 最后更新时间 */
    private LocalDateTime lastUpdateTime;

    // Getters and Setters
    public Integer getResumeCompleteness() {
        return resumeCompleteness;
    }

    public void setResumeCompleteness(Integer resumeCompleteness) {
        this.resumeCompleteness = resumeCompleteness;
    }

    public Integer getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(Integer totalSkills) {
        this.totalSkills = totalSkills;
    }

    public Integer getExpertSkills() {
        return expertSkills;
    }

    public void setExpertSkills(Integer expertSkills) {
        this.expertSkills = expertSkills;
    }

    public Integer getEducationCount() {
        return educationCount;
    }

    public void setEducationCount(Integer educationCount) {
        this.educationCount = educationCount;
    }

    public Integer getWorkExperienceCount() {
        return workExperienceCount;
    }

    public void setWorkExperienceCount(Integer workExperienceCount) {
        this.workExperienceCount = workExperienceCount;
    }

    public Integer getAssessmentCount() {
        return assessmentCount;
    }

    public void setAssessmentCount(Integer assessmentCount) {
        this.assessmentCount = assessmentCount;
    }

    public Integer getLearningRecordCount() {
        return learningRecordCount;
    }

    public void setLearningRecordCount(Integer learningRecordCount) {
        this.learningRecordCount = learningRecordCount;
    }

    public Double getTotalLearningHours() {
        return totalLearningHours;
    }

    public void setTotalLearningHours(Double totalLearningHours) {
        this.totalLearningHours = totalLearningHours;
    }

    public Double getWeeklyLearningHours() {
        return weeklyLearningHours;
    }

    public void setWeeklyLearningHours(Double weeklyLearningHours) {
        this.weeklyLearningHours = weeklyLearningHours;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
