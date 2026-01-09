package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 简历实体类
 */
public class Resume implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 简历ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 简历标题
     */
    private String title;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 目标职位
     */
    private String targetPosition;

    /**
     * 期望薪资
     */
    private String expectedSalary;

    /**
     * 工作城市
     */
    private String workCity;

    /**
     * 最高学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 毕业年份
     */
    private Integer graduationYear;

    /**
     * 工作经验（年）
     */
    private Integer workExperience;

    /**
     * 自我介绍
     */
    private String selfIntroduction;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== 关联对象 ====================

    /**
     * 技能列表
     */
    private List<Skill> skills;

    /**
     * 工作经历列表
     */
    private List<WorkExperience> workExperiences;

    /**
     * 教育经历列表
     */
    private List<Education> educations;

    // ==================== Constructors ====================

    public Resume() {
    }

    public Resume(Long userId, String title) {
        this.userId = userId;
        this.title = title;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getWorkCity() {
        return workCity;
    }

    public void setWorkCity(String workCity) {
        this.workCity = workCity;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Integer getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(Integer workExperience) {
        this.workExperience = workExperience;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
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

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }

    @Override
    public String toString() {
        return "Resume{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", realName='" + realName + '\'' +
                ", targetPosition='" + targetPosition + '\'' +
                '}';
    }
}
