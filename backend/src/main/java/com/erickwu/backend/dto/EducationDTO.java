package com.erickwu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 添加教育经历请求DTO
 */
public class EducationDTO {

    @NotBlank(message = "学校名称不能为空")
    @Size(max = 100, message = "学校名称最多100个字符")
    private String school;

    @NotBlank(message = "学位不能为空")
    @Size(max = 50, message = "学位最多50个字符")
    private String degree;

    @Size(max = 100, message = "专业最多100个字符")
    private String major;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal gpa;

    @Size(max = 1000, message = "在校经历最多1000个字符")
    private String description;

    // ==================== Getters and Setters ====================

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
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

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
