package com.erickwu.backend.service;

import com.erickwu.backend.dto.*;
import com.erickwu.backend.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 简历服务接口
 */
public interface ResumeService {

    // ==================== 简历基本操作 ====================

    /**
     * 创建简历
     * @param userId 用户ID
     * @param dto 简历信息
     * @return 创建的简历详情
     */
    ResumeDetailVO createResume(Long userId, ResumeDTO dto);

    /**
     * 获取简历详情（包含技能、教育、工作经历）
     * @param resumeId 简历ID
     * @return 简历详情
     */
    ResumeDetailVO getResumeDetail(Long resumeId);

    /**
     * 根据用户ID获取简历详情
     * @param userId 用户ID
     * @return 简历详情
     */
    ResumeDetailVO getResumeByUserId(Long userId);

    /**
     * 更新简历基本信息
     * @param resumeId 简历ID
     * @param dto 简历信息
     */
    void updateResume(Long resumeId, ResumeDTO dto);

    /**
     * 删除简历（同时删除关联的技能、教育、工作经历）
     * @param resumeId 简历ID
     */
    void deleteResume(Long resumeId);

    // ==================== 技能管理 ====================

    /**
     * 添加技能
     * @param resumeId 简历ID
     * @param dto 技能信息
     * @return 创建的技能
     */
    SkillVO addSkill(Long resumeId, SkillDTO dto);

    /**
     * 批量添加技能
     * @param resumeId 简历ID
     * @param dtoList 技能列表
     * @return 创建的技能列表
     */
    List<SkillVO> batchAddSkills(Long resumeId, List<SkillDTO> dtoList);

    /**
     * 更新技能
     * @param skillId 技能ID
     * @param dto 技能信息
     */
    void updateSkill(Long skillId, SkillDTO dto);

    /**
     * 删除技能
     * @param skillId 技能ID
     */
    void deleteSkill(Long skillId);

    /**
     * 获取简历的技能列表
     * @param resumeId 简历ID
     * @return 技能列表
     */
    List<SkillVO> getSkillsByResumeId(Long resumeId);

    // ==================== 教育经历管理 ====================

    /**
     * 添加教育经历
     * @param resumeId 简历ID
     * @param dto 教育经历信息
     * @return 创建的教育经历
     */
    EducationVO addEducation(Long resumeId, EducationDTO dto);

    /**
     * 更新教育经历
     * @param educationId 教育经历ID
     * @param dto 教育经历信息
     */
    void updateEducation(Long educationId, EducationDTO dto);

    /**
     * 删除教育经历
     * @param educationId 教育经历ID
     */
    void deleteEducation(Long educationId);

    /**
     * 获取简历的教育经历列表
     * @param resumeId 简历ID
     * @return 教育经历列表
     */
    List<EducationVO> getEducationsByResumeId(Long resumeId);

    // ==================== 工作经历管理 ====================

    /**
     * 添加工作经历
     * @param resumeId 简历ID
     * @param dto 工作经历信息
     * @return 创建的工作经历
     */
    WorkExperienceVO addWorkExperience(Long resumeId, WorkExperienceDTO dto);

    /**
     * 更新工作经历
     * @param workExperienceId 工作经历ID
     * @param dto 工作经历信息
     */
    void updateWorkExperience(Long workExperienceId, WorkExperienceDTO dto);

    /**
     * 删除工作经历
     * @param workExperienceId 工作经历ID
     */
    void deleteWorkExperience(Long workExperienceId);

    /**
     * 获取简历的工作经历列表
     * @param resumeId 简历ID
     * @return 工作经历列表
     */
    List<WorkExperienceVO> getWorkExperiencesByResumeId(Long resumeId);

    // ==================== 版本管理 ====================

    /**
     * 获取简历版本列表
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 版本列表
     */
    List<ResumeVersionVO> getVersionsByUserId(Long userId, int limit);

    /**
     * 获取版本详情
     * @param versionId 版本ID
     * @return 版本详情
     */
    ResumeVersionDetailVO getVersionDetail(Long versionId);

    /**
     * 删除版本
     * @param versionId 版本ID
     */
    void deleteVersion(Long versionId);

    // ==================== 简历上传解析 ====================

    /**
     * 上传并解析简历文件
     * @param userId 用户ID
     * @param file 简历文件
     * @param versionNote 版本备注
     * @return 解析结果
     */
    ResumeParseResultVO uploadAndParseResume(Long userId, MultipartFile file, String versionNote) throws IOException;

    /**
     * 将解析结果保存到简历
     * @param userId 用户ID
     * @param parseResult 解析结果
     * @return 保存后的简历详情
     */
    ResumeDetailVO saveParseResult(Long userId, ResumeParseResultVO parseResult);
}
