package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.ResumeVersion;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 简历版本 Mapper 接口
 */
@Mapper
public interface ResumeVersionMapper {

    /**
     * 根据ID查询版本
     */
    @Select("SELECT * FROM resume_version WHERE id = #{id}")
    ResumeVersion findById(@Param("id") Long id);

    /**
     * 根据简历ID查询版本列表（按上传时间倒序）
     */
    @Select("SELECT * FROM resume_version WHERE resume_id = #{resumeId} ORDER BY upload_time DESC LIMIT #{limit}")
    List<ResumeVersion> findByResumeId(@Param("resumeId") Long resumeId, @Param("limit") int limit);

    /**
     * 获取指定简历的最新版本
     */
    @Select("SELECT * FROM resume_version WHERE resume_id = #{resumeId} ORDER BY version_number DESC LIMIT 1")
    ResumeVersion findLatestByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 获取指定简历的最大版本号
     */
    @Select("SELECT MAX(version_number) FROM resume_version WHERE resume_id = #{resumeId}")
    Integer getMaxVersionNumber(@Param("resumeId") Long resumeId);

    /**
     * 插入版本记录
     */
    @Insert("INSERT INTO resume_version (resume_id, version_number, file_name, file_size, raw_text, " +
            "parsed_data, analysis_report, analysis_metadata, upload_time, version_note) " +
            "VALUES (#{resumeId}, #{versionNumber}, #{fileName}, #{fileSize}, #{rawText}, " +
            "#{parsedData}, #{analysisReport}, #{analysisMetadata}, #{uploadTime}, #{versionNote})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResumeVersion version);

    /**
     * 更新版本记录
     */
    @Update("UPDATE resume_version SET analysis_report = #{analysisReport}, " +
            "analysis_metadata = #{analysisMetadata}, version_note = #{versionNote} " +
            "WHERE id = #{id}")
    int update(ResumeVersion version);

    /**
     * 删除版本
     */
    @Delete("DELETE FROM resume_version WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据简历ID删除所有版本
     */
    @Delete("DELETE FROM resume_version WHERE resume_id = #{resumeId}")
    int deleteByResumeId(@Param("resumeId") Long resumeId);

    /**
     * 统计简历版本数量
     */
    @Select("SELECT COUNT(*) FROM resume_version WHERE resume_id = #{resumeId}")
    int countByResumeId(@Param("resumeId") Long resumeId);
}
