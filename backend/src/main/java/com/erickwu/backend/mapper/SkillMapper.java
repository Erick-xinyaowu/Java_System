package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 技能 Mapper 接口
 */
@Mapper
public interface SkillMapper {

    /**
     * 根据ID查询技能
     */
    @Select("SELECT * FROM skill WHERE id = #{id}")
    Skill findById(@Param("id") Long id);

    /**
     * 根据简历ID查询技能列表
     */
    @Select("SELECT * FROM skill WHERE resume_id = #{resumeId} ORDER BY level DESC, years DESC")
    List<Skill> findByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 根据分类查询技能
     */
    @Select("SELECT * FROM skill WHERE resume_id = #{resumeId} AND category = #{category}")
    List<Skill> findByResumeIdAndCategory(@Param("resumeId") Long resumeId, @Param("category") String category);

    /**
     * 插入技能
     */
    @Insert("INSERT INTO skill (resume_id, name, level, category, years) " +
            "VALUES (#{resumeId}, #{name}, #{level}, #{category}, #{years})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Skill skill);

    /**
     * 批量插入技能
     */
    @Insert("<script>" +
            "INSERT INTO skill (resume_id, name, level, category, years) VALUES " +
            "<foreach collection='skills' item='skill' separator=','>" +
            "(#{skill.resumeId}, #{skill.name}, #{skill.level}, #{skill.category}, #{skill.years})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("skills") List<Skill> skills);

    /**
     * 更新技能
     */
    @Update("UPDATE skill SET name = #{name}, level = #{level}, category = #{category}, " +
            "years = #{years} WHERE id = #{id}")
    int update(Skill skill);

    /**
     * 删除技能
     */
    @Delete("DELETE FROM skill WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据简历ID删除所有技能
     */
    @Delete("DELETE FROM skill WHERE resume_id = #{resumeId}")
    int deleteByResumeId(@Param("resumeId") Long resumeId);
}
