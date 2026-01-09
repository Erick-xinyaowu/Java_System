package com.erickwu.backend.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 简历解析结果 VO
 * 包含从简历文件中提取的结构化信息
 */
public class ResumeParseResultVO {

    // ==================== 文件信息 ====================
    private String fileName;
    private Long fileSize;
    private String rawText;
    private String parsedJson;

    // ==================== 基本信息 ====================
    private String candidateName;
    private String phone;
    private String email;
    private String address;
    private String targetPosition;
    private String summary;

    // ==================== 结构化数据 ====================
    private List<SkillInfo> skills;
    private List<EducationInfo> educations;
    private List<WorkExperienceInfo> workExperiences;

    // ==================== 内部类 ====================

    /**
     * 技能信息
     */
    public static class SkillInfo {
        private String name;
        private Integer level;  // 1-5
        private String category;
        private Integer years;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getYears() { return years; }
        public void setYears(Integer years) { this.years = years; }

        /**
         * 获取技能等级名称
         */
        public String getLevelName() {
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

    /**
     * 教育经历信息
     */
    public static class EducationInfo {
        private String school;
        private String degree;
        private String major;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal gpa;
        private String description;

        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public BigDecimal getGpa() { return gpa; }
        public void setGpa(BigDecimal gpa) { this.gpa = gpa; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 工作经历信息
     */
    public static class WorkExperienceInfo {
        private String company;
        private String position;
        private String department;
        private LocalDate startDate;
        private LocalDate endDate;
        private String description;
        private String achievements;

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAchievements() { return achievements; }
        public void setAchievements(String achievements) { this.achievements = achievements; }
    }

    // ==================== Getters and Setters ====================

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getParsedJson() { return parsedJson; }
    public void setParsedJson(String parsedJson) { this.parsedJson = parsedJson; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTargetPosition() { return targetPosition; }
    public void setTargetPosition(String targetPosition) { this.targetPosition = targetPosition; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<SkillInfo> getSkills() { return skills; }
    public void setSkills(List<SkillInfo> skills) { this.skills = skills; }

    public List<EducationInfo> getEducations() { return educations; }
    public void setEducations(List<EducationInfo> educations) { this.educations = educations; }

    public List<WorkExperienceInfo> getWorkExperiences() { return workExperiences; }
    public void setWorkExperiences(List<WorkExperienceInfo> workExperiences) { this.workExperiences = workExperiences; }
}
