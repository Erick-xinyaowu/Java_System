package com.erickwu.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习记录实体类
 */
public class LearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程/学习内容名称
     */
    private String courseName;

    /**
     * 分类
     */
    private String category;

    /**
     * 学习平台
     */
    private String platform;

    /**
     * 学习进度（百分比）
     */
    private Integer progress;

    /**
     * 总学时
     */
    private BigDecimal totalHours;

    /**
     * 已完成学时
     */
    private BigDecimal completedHours;

    /**
     * 状态：0-暂停 1-进行中 2-已完成
     */
    private Integer status;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 证书URL
     */
    private String certificate;

    /**
     * 学习笔记
     */
    private String notes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== Constructors ====================

    public LearningRecord() {
    }

    public LearningRecord(Long userId, String courseName, String category) {
        this.userId = userId;
        this.courseName = courseName;
        this.category = category;
        this.progress = 0;
        this.status = 1;
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getCompletedHours() {
        return completedHours;
    }

    public void setCompletedHours(BigDecimal completedHours) {
        this.completedHours = completedHours;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return switch (status) {
            case 0 -> "暂停";
            case 1 -> "进行中";
            case 2 -> "已完成";
            default -> "未知";
        };
    }

    @Override
    public String toString() {
        return "LearningRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", courseName='" + courseName + '\'' +
                ", progress=" + progress +
                ", status=" + status +
                '}';
    }
}
