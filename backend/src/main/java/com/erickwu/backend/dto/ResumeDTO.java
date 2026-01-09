package com.erickwu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建/更新简历请求DTO
 */
public class ResumeDTO {

    @NotBlank(message = "简历标题不能为空")
    @Size(max = 100, message = "简历标题最多100个字符")
    private String title;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名最多50个字符")
    private String realName;

    @Size(max = 100, message = "目标职位最多100个字符")
    private String targetPosition;

    @Size(max = 50, message = "期望薪资最多50个字符")
    private String expectedSalary;

    @Size(max = 50, message = "工作城市最多50个字符")
    private String workCity;

    @Size(max = 20, message = "学历最多20个字符")
    private String education;

    @Size(max = 100, message = "学校名称最多100个字符")
    private String school;

    @Size(max = 100, message = "专业最多100个字符")
    private String major;

    private Integer graduationYear;

    private Integer workExperience;

    @Size(max = 2000, message = "自我介绍最多2000个字符")
    private String selfIntroduction;

    // ==================== Getters and Setters ====================

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
}
