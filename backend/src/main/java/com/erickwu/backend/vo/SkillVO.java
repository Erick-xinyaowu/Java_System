package com.erickwu.backend.vo;

import java.time.LocalDateTime;

/**
 * 技能响应VO
 */
public class SkillVO {

    private Long id;
    private Long resumeId;
    private String name;
    private Integer level;
    private String levelName;  // 熟练度名称
    private String category;
    private Integer years;
    private LocalDateTime createdAt;

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
        // 自动设置熟练度名称
        this.levelName = getLevelNameByLevel(level);
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
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
     * 根据熟练度等级获取名称
     */
    private String getLevelNameByLevel(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 1 -> "了解";
            case 2 -> "熟悉";
            case 3 -> "掌握";
            case 4 -> "精通";
            case 5 -> "专家";
            default -> "未知";
        };
    }
}
