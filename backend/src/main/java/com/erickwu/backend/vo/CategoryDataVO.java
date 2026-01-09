package com.erickwu.backend.vo;

/**
 * 分类数据VO
 * 用于饼图、柱状图展示
 */
public class CategoryDataVO {
    
    /** 分类名称 */
    private String name;
    
    /** 数值 */
    private Double value;
    
    /** 百分比 */
    private Double percentage;
    
    /** 颜色（可选，用于前端图表） */
    private String color;

    public CategoryDataVO() {
    }

    public CategoryDataVO(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public CategoryDataVO(String name, Double value, Double percentage) {
        this.name = name;
        this.value = value;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
