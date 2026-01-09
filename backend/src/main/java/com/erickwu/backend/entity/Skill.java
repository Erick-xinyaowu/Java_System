package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技能实体类
 */
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 技能ID
     */
    private Long id;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 技能名称
     */
    private String name;

    /**
     * 熟练度：1-了解 2-熟悉 3-掌握 4-精通 5-专家
     */
    private Integer level;

    /**
     * 技能分类
     */
    private String category;

    /**
     * 使用年限
     */
    private Integer years;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== Constructors ====================

    public Skill() {
    }

    public Skill(Long resumeId, String name, Integer level, String category) {
        this.resumeId = resumeId;
        this.name = name;
        this.level = level;
        this.category = category;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取技能等级描述
     */
    public String getLevelDescription() {
        return switch (level) {
            case 1 -> "了解";
            case 2 -> "熟悉";
            case 3 -> "掌握";
            case 4 -> "精通";
            case 5 -> "专家";
            default -> "未知";
        };
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", category='" + category + '\'' +
                '}';
    }
}
