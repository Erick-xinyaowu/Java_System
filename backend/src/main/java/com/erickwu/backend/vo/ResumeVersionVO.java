package com.erickwu.backend.vo;

import java.time.LocalDateTime;

/**
 * 简历版本响应VO
 */
public class ResumeVersionVO {

    private Long id;
    private Long resumeId;
    private Integer versionNumber;
    private String fileName;
    private Long fileSize;
    private LocalDateTime uploadTime;
    private String versionNote;
    
    /**
     * 候选人姓名
     */
    private String candidateName;
    
    /**
     * 是否有分析报告
     */
    private Boolean hasAnalysis;

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

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getVersionNote() {
        return versionNote;
    }

    public void setVersionNote(String versionNote) {
        this.versionNote = versionNote;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public Boolean getHasAnalysis() {
        return hasAnalysis;
    }

    public void setHasAnalysis(Boolean hasAnalysis) {
        this.hasAnalysis = hasAnalysis;
    }
}
