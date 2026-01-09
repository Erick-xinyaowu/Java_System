package com.erickwu.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育经历实体类
 */
public class Education implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教育ID
     */
    private Long id;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 学位
     */
    private String degree;

    /**
     * 专业
     */
    private String major;

    /**
     * 入学日期
     */
    private LocalDate startDate;

    /**
     * 毕业日期
     */
    private LocalDate endDate;

    /**
     * GPA
     */
    private BigDecimal gpa;

    /**
     * 在校经历
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== Constructors ====================

    public Education() {
    }

    public Education(Long resumeId, String school, String degree, LocalDate startDate) {
        this.resumeId = resumeId;
        this.school = school;
        this.degree = degree;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Education{" +
                "id=" + id +
                ", school='" + school + '\'' +
                ", degree='" + degree + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}
