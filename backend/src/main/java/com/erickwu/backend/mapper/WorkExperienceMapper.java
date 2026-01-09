package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.WorkExperience;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 工作经历 Mapper 接口
 */
@Mapper
public interface WorkExperienceMapper {

    /**
     * 根据ID查询工作经历
     */
    @Select("SELECT * FROM work_experience WHERE id = #{id}")
    WorkExperience findById(@Param("id") Long id);

    /**
     * 根据简历ID查询工作经历列表（按开始日期倒序）
     */
    @Select("SELECT * FROM work_experience WHERE resume_id = #{resumeId} ORDER BY start_date DESC")
    List<WorkExperience> findByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 插入工作经历
     */
    @Insert("INSERT INTO work_experience (resume_id, company, position, department, start_date, end_date, description, achievements) " +
            "VALUES (#{resumeId}, #{company}, #{position}, #{department}, #{startDate}, #{endDate}, #{description}, #{achievements})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WorkExperience workExperience);

    /**
     * 批量插入工作经历
     */
    @Insert("<script>" +
            "INSERT INTO work_experience (resume_id, company, position, department, start_date, end_date, description, achievements) VALUES " +
            "<foreach collection='workExperiences' item='we' separator=','>" +
            "(#{we.resumeId}, #{we.company}, #{we.position}, #{we.department}, #{we.startDate}, #{we.endDate}, #{we.description}, #{we.achievements})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("workExperiences") List<WorkExperience> workExperiences);

    /**
     * 更新工作经历
     */
    @Update("UPDATE work_experience SET company = #{company}, position = #{position}, department = #{department}, " +
            "start_date = #{startDate}, end_date = #{endDate}, description = #{description}, achievements = #{achievements} " +
            "WHERE id = #{id}")
    int update(WorkExperience workExperience);

    /**
     * 删除工作经历
     */
    @Delete("DELETE FROM work_experience WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据简历ID删除所有工作经历
     */
    @Delete("DELETE FROM work_experience WHERE resume_id = #{resumeId}")
    int deleteByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 统计简历的工作经历数量
     */
    @Select("SELECT COUNT(*) FROM work_experience WHERE resume_id = #{resumeId}")
    int countByResumeId(@Param("resumeId") Long resumeId);
}
