package com.erickwu.backend.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 简历详情响应VO
 */
public class ResumeDetailVO {

    private Long id;
    private Long userId;
    private String title;
    private String realName;
    private String targetPosition;
    private String expectedSalary;
    private String workCity;
    private String education;
    private String school;
    private String major;
    private Integer graduationYear;
    private Integer workExperience;
    private String selfIntroduction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 关联的技能列表
     */
    private List<SkillVO> skills;

    /**
     * 关联的教育经历列表
     */
    private List<EducationVO> educations;

    /**
     * 关联的工作经历列表
     */
    private List<WorkExperienceVO> workExperiences;

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

    public List<SkillVO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillVO> skills) {
        this.skills = skills;
    }

    public List<EducationVO> getEducations() {
        return educations;
    }

    public void setEducations(List<EducationVO> educations) {
        this.educations = educations;
    }

    public List<WorkExperienceVO> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<WorkExperienceVO> workExperiences) {
        this.workExperiences = workExperiences;
    }
}
