package com.erickwu.backend.vo;

import java.time.LocalDateTime;

/**
 * 最近活动VO
 * 用于展示用户最近的操作记录
 */
public class RecentActivityVO {
    
    /** 活动类型：resume, skill, education, work, assessment, learning, report */
    private String type;
    
    /** 活动标题 */
    private String title;
    
    /** 活动描述 */
    private String description;
    
    /** 活动时间 */
    private LocalDateTime time;
    
    /** 图标（前端使用） */
    private String icon;

    public RecentActivityVO() {
    }

    public RecentActivityVO(String type, String title, String description, LocalDateTime time) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.time = time;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
