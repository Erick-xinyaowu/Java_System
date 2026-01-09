package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 职业报告实体类
 */
public class CareerReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报告标题
     */
    private String title;

    /**
     * 报告类型：AI生成/手动创建
     */
    private String type;

    /**
     * 报告内容（Markdown格式）
     */
    private String content;

    /**
     * 报告摘要
     */
    private String summary;

    /**
     * 依据数据（JSON字符串）
     */
    private String basedOn;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== Constructors ====================

    public CareerReport() {
    }

    public CareerReport(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = "AI生成";
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(String basedOn) {
        this.basedOn = basedOn;
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

    @Override
    public String toString() {
        return "CareerReport{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
