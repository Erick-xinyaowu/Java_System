package com.erickwu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 添加工作经历请求DTO
 */
public class WorkExperienceDTO {

    @NotBlank(message = "公司名称不能为空")
    @Size(max = 100, message = "公司名称最多100个字符")
    private String company;

    @NotBlank(message = "职位不能为空")
    @Size(max = 100, message = "职位最多100个字符")
    private String position;

    @Size(max = 100, message = "部门最多100个字符")
    private String department;

    private LocalDate startDate;

    /**
     * 结束日期，NULL表示至今
     */
    private LocalDate endDate;

    @Size(max = 2000, message = "工作描述最多2000个字符")
    private String description;

    @Size(max = 1000, message = "主要成就最多1000个字符")
    private String achievements;

    // ==================== Getters and Setters ====================

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
}
