package com.erickwu.backend.vo;

import java.time.LocalDateTime;

/**
 * 简历版本详情响应VO
 * 包含完整的版本信息、原始文本、解析数据和分析报告
 */
public class ResumeVersionDetailVO {

    private Long id;
    private Long resumeId;
    private Integer versionNumber;
    private String fileName;
    private Long fileSize;
    private String rawText;
    private String parsedData;  // JSON格式
    private String analysisReport;  // Markdown格式
    private String analysisMetadata;  // JSON格式
    private LocalDateTime uploadTime;
    private String versionNote;
    
    /**
     * 候选人姓名
     */
    private String candidateName;

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

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getParsedData() {
        return parsedData;
    }

    public void setParsedData(String parsedData) {
        this.parsedData = parsedData;
    }

    public String getAnalysisReport() {
        return analysisReport;
    }

    public void setAnalysisReport(String analysisReport) {
        this.analysisReport = analysisReport;
    }

    public String getAnalysisMetadata() {
        return analysisMetadata;
    }

    public void setAnalysisMetadata(String analysisMetadata) {
        this.analysisMetadata = analysisMetadata;
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

    /**
     * 判断是否有分析报告
     */
    public boolean isHasAnalysis() {
        return analysisReport != null && !analysisReport.isEmpty();
    }
}
