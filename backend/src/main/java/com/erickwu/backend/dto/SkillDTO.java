package com.erickwu.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 添加技能请求DTO
 */
public class SkillDTO {

    @NotBlank(message = "技能名称不能为空")
    @Size(max = 50, message = "技能名称最多50个字符")
    private String name;

    /**
     * 熟练度：1-了解 2-熟悉 3-掌握 4-精通 5-专家
     */
    private Integer level;

    @Size(max = 50, message = "技能分类最多50个字符")
    private String category;

    /**
     * 使用年限
     */
    private Integer years;

    // ==================== Getters and Setters ====================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }
}
