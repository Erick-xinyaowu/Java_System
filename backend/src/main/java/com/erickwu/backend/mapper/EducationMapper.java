package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.Education;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 教育经历 Mapper 接口
 */
@Mapper
public interface EducationMapper {

    /**
     * 根据ID查询教育经历
     */
    @Select("SELECT * FROM education WHERE id = #{id}")
    Education findById(@Param("id") Long id);

    /**
     * 根据简历ID查询教育经历列表（按开始日期倒序）
     */
    @Select("SELECT * FROM education WHERE resume_id = #{resumeId} ORDER BY start_date DESC")
    List<Education> findByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 插入教育经历
     */
    @Insert("INSERT INTO education (resume_id, school, degree, major, start_date, end_date, gpa, description) " +
            "VALUES (#{resumeId}, #{school}, #{degree}, #{major}, #{startDate}, #{endDate}, #{gpa}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Education education);

    /**
     * 批量插入教育经历
     */
    @Insert("<script>" +
            "INSERT INTO education (resume_id, school, degree, major, start_date, end_date, gpa, description) VALUES " +
            "<foreach collection='educations' item='edu' separator=','>" +
            "(#{edu.resumeId}, #{edu.school}, #{edu.degree}, #{edu.major}, #{edu.startDate}, #{edu.endDate}, #{edu.gpa}, #{edu.description})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("educations") List<Education> educations);

    /**
     * 更新教育经历
     */
    @Update("UPDATE education SET school = #{school}, degree = #{degree}, major = #{major}, " +
            "start_date = #{startDate}, end_date = #{endDate}, gpa = #{gpa}, description = #{description} " +
            "WHERE id = #{id}")
    int update(Education education);

    /**
     * 删除教育经历
     */
    @Delete("DELETE FROM education WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据简历ID删除所有教育经历
     */
    @Delete("DELETE FROM education WHERE resume_id = #{resumeId}")
    int deleteByResumeId(@Param("resumeId") Long resumeId);
}
