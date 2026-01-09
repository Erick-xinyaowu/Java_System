package com.erickwu.backend.vo;

/**
 * 趋势数据VO
 * 用于折线图展示
 */
public class TrendDataVO {
    
    /** 日期标签（如 "01-01", "周一" 等） */
    private String date;
    
    /** 数值 */
    private Double value;
    
    /** 数据类型（如 "学习时长", "完成课程" 等） */
    private String type;

    public TrendDataVO() {
    }

    public TrendDataVO(String date, Double value) {
        this.date = date;
        this.value = value;
    }

    public TrendDataVO(String date, Double value, String type) {
        this.date = date;
        this.value = value;
        this.type = type;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
