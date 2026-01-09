package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作经历实体类
 */
public class WorkExperience implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 经历ID
     */
    private Long id;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位
     */
    private String position;

    /**
     * 部门
     */
    private String department;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期（NULL表示至今）
     */
    private LocalDate endDate;

    /**
     * 工作描述
     */
    private String description;

    /**
     * 主要成就
     */
    private String achievements;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== Constructors ====================

    public WorkExperience() {
    }

    public WorkExperience(Long resumeId, String company, String position, LocalDate startDate) {
        this.resumeId = resumeId;
        this.company = company;
        this.position = position;
        this.startDate = startDate;
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 是否是当前工作
     */
    public boolean isCurrent() {
        return endDate == null;
    }

    @Override
    public String toString() {
        return "WorkExperience{" +
                "id=" + id +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
