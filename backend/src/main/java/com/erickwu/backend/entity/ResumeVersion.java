package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 简历版本历史实体类
 * 存储每次上传的版本快照
 */
public class ResumeVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    private Long id;

    /**
     * 关联简历ID
     */
    private Long resumeId;

    /**
     * 版本号 (1, 2, 3...)
     */
    private Integer versionNumber;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 原始简历文本
     */
    private String rawText;

    /**
     * 解析后的结构化数据（JSON格式）
     */
    private String parsedData;

    /**
     * AI分析报告（Markdown格式）
     */
    private String analysisReport;

    /**
     * 分析元数据（Token使用量等，JSON格式）
     */
    private String analysisMetadata;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 版本说明
     */
    private String versionNote;

    // ==================== Constructors ====================

    public ResumeVersion() {
    }

    public ResumeVersion(Long resumeId, Integer versionNumber, String fileName) {
        this.resumeId = resumeId;
        this.versionNumber = versionNumber;
        this.fileName = fileName;
        this.uploadTime = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "ResumeVersion{" +
                "id=" + id +
                ", resumeId=" + resumeId +
                ", versionNumber=" + versionNumber +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", uploadTime=" + uploadTime +
                ", versionNote='" + versionNote + '\'' +
                '}';
    }
}
