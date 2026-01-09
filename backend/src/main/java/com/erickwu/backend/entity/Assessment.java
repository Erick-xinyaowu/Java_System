package com.erickwu.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评记录实体类
 */
public class Assessment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测评ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 测评类型：MBTI/HOLLAND/DISC
     */
    private String type;

    /**
     * 结果代码，如INTJ、IAR等
     */
    private String resultCode;

    /**
     * 结果名称，如"建筑师"
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String description;

    /**
     * 优势列表（JSON字符串）
     */
    private String strengths;

    /**
     * 推荐职业（JSON字符串）
     */
    private String careers;

    /**
     * 发展建议（JSON字符串）
     */
    private String suggestions;

    /**
     * 答题记录（JSON字符串）
     */
    private String answers;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== 转换后的列表（非数据库字段） ====================

    private transient List<String> strengthsList;
    private transient List<String> careersList;
    private transient List<String> suggestionsList;

    // ==================== Constructors ====================

    public Assessment() {
    }

    public Assessment(Long userId, String type) {
        this.userId = userId;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getCareers() {
        return careers;
    }

    public void setCareers(String careers) {
        this.careers = careers;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getStrengthsList() {
        return strengthsList;
    }

    public void setStrengthsList(List<String> strengthsList) {
        this.strengthsList = strengthsList;
    }

    public List<String> getCareersList() {
        return careersList;
    }

    public void setCareersList(List<String> careersList) {
        this.careersList = careersList;
    }

    public List<String> getSuggestionsList() {
        return suggestionsList;
    }

    public void setSuggestionsList(List<String> suggestionsList) {
        this.suggestionsList = suggestionsList;
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultName='" + resultName + '\'' +
                ", score=" + score +
                '}';
    }
}
