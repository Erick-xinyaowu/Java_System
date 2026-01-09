package com.erickwu.backend.controller;

import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.util.UserContext;
import com.erickwu.backend.dto.*;
import com.erickwu.backend.service.ResumeService;
import com.erickwu.backend.vo.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 简历控制器
 * 提供简历管理的 RESTful API
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // ==================== 简历上传解析 ====================

    /**
     * 上传并解析简历
     * POST /api/resume/upload
     */
    @PostMapping("/upload")
    public ApiResponse<ResumeParseResultVO> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String versionNote) throws IOException {
        Long userId = UserContext.getCurrentUserId();
        logger.info("用户 {} 上传简历文件: {}", userId, file.getOriginalFilename());
        
        ResumeParseResultVO result = resumeService.uploadAndParseResume(userId, file, versionNote);
        return ApiResponse.success("简历解析成功", result);
    }

    /**
     * 确认保存解析结果到简历
     * POST /api/resume/save-parsed
     */
    @PostMapping("/save-parsed")
    public ApiResponse<ResumeDetailVO> saveParsedResume(@RequestBody ResumeParseResultVO parseResult) {
        Long userId = UserContext.getCurrentUserId();
        ResumeDetailVO result = resumeService.saveParseResult(userId, parseResult);
        return ApiResponse.success("简历保存成功", result);
    }

    // ==================== 简历基本操作 ====================

    /**
     * 创建简历
     * POST /api/resume
     */
    @PostMapping
    public ApiResponse<ResumeDetailVO> createResume(@Valid @RequestBody ResumeDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        ResumeDetailVO result = resumeService.createResume(userId, dto);
        return ApiResponse.success("简历创建成功", result);
    }

    /**
     * 获取当前用户的简历
     * GET /api/resume
     */
    @GetMapping
    public ApiResponse<ResumeDetailVO> getMyResume() {
        Long userId = UserContext.getCurrentUserId();
        ResumeDetailVO result = resumeService.getResumeByUserId(userId);
        if (result == null) {
            return ApiResponse.success("用户暂无简历", null);
        }
        return ApiResponse.success(result);
    }

    /**
     * 获取简历详情
     * GET /api/resume/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<ResumeDetailVO> getResumeDetail(@PathVariable Long id) {
        ResumeDetailVO result = resumeService.getResumeDetail(id);
        return ApiResponse.success(result);
    }

    /**
     * 更新简历
     * PUT /api/resume/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> updateResume(@PathVariable Long id, @Valid @RequestBody ResumeDTO dto) {
        resumeService.updateResume(id, dto);
        return ApiResponse.success("简历更新成功", null);
    }

    /**
     * 删除简历
     * DELETE /api/resume/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ApiResponse.success("简历删除成功", null);
    }

    // ==================== 技能管理 ====================

    /**
     * 获取简历技能列表
     * GET /api/resume/{resumeId}/skills
     */
    @GetMapping("/{resumeId}/skills")
    public ApiResponse<List<SkillVO>> getSkills(@PathVariable Long resumeId) {
        List<SkillVO> result = resumeService.getSkillsByResumeId(resumeId);
        return ApiResponse.success(result);
    }

    /**
     * 添加技能
     * POST /api/resume/{resumeId}/skill
     */
    @PostMapping("/{resumeId}/skill")
    public ApiResponse<SkillVO> addSkill(@PathVariable Long resumeId, @Valid @RequestBody SkillDTO dto) {
        SkillVO result = resumeService.addSkill(resumeId, dto);
        return ApiResponse.success("技能添加成功", result);
    }

    /**
     * 批量添加技能
     * POST /api/resume/{resumeId}/skills
     */
    @PostMapping("/{resumeId}/skills")
    public ApiResponse<List<SkillVO>> batchAddSkills(@PathVariable Long resumeId, @Valid @RequestBody List<SkillDTO> dtoList) {
        List<SkillVO> result = resumeService.batchAddSkills(resumeId, dtoList);
        return ApiResponse.success("技能批量添加成功", result);
    }

    /**
     * 更新技能
     * PUT /api/resume/skill/{skillId}
     */
    @PutMapping("/skill/{skillId}")
    public ApiResponse<Void> updateSkill(@PathVariable Long skillId, @Valid @RequestBody SkillDTO dto) {
        resumeService.updateSkill(skillId, dto);
        return ApiResponse.success("技能更新成功", null);
    }

    /**
     * 删除技能
     * DELETE /api/resume/skill/{skillId}
     */
    @DeleteMapping("/skill/{skillId}")
    public ApiResponse<Void> deleteSkill(@PathVariable Long skillId) {
        resumeService.deleteSkill(skillId);
        return ApiResponse.success("技能删除成功", null);
    }

    // ==================== 教育经历管理 ====================

    /**
     * 获取简历教育经历列表
     * GET /api/resume/{resumeId}/educations
     */
    @GetMapping("/{resumeId}/educations")
    public ApiResponse<List<EducationVO>> getEducations(@PathVariable Long resumeId) {
        List<EducationVO> result = resumeService.getEducationsByResumeId(resumeId);
        return ApiResponse.success(result);
    }

    /**
     * 添加教育经历
     * POST /api/resume/{resumeId}/education
     */
    @PostMapping("/{resumeId}/education")
    public ApiResponse<EducationVO> addEducation(@PathVariable Long resumeId, @Valid @RequestBody EducationDTO dto) {
        EducationVO result = resumeService.addEducation(resumeId, dto);
        return ApiResponse.success("教育经历添加成功", result);
    }

    /**
     * 更新教育经历
     * PUT /api/resume/education/{educationId}
     */
    @PutMapping("/education/{educationId}")
    public ApiResponse<Void> updateEducation(@PathVariable Long educationId, @Valid @RequestBody EducationDTO dto) {
        resumeService.updateEducation(educationId, dto);
        return ApiResponse.success("教育经历更新成功", null);
    }

    /**
     * 删除教育经历
     * DELETE /api/resume/education/{educationId}
     */
    @DeleteMapping("/education/{educationId}")
    public ApiResponse<Void> deleteEducation(@PathVariable Long educationId) {
        resumeService.deleteEducation(educationId);
        return ApiResponse.success("教育经历删除成功", null);
    }

    // ==================== 工作经历管理 ====================

    /**
     * 获取简历工作经历列表
     * GET /api/resume/{resumeId}/work-experiences
     */
    @GetMapping("/{resumeId}/work-experiences")
    public ApiResponse<List<WorkExperienceVO>> getWorkExperiences(@PathVariable Long resumeId) {
        List<WorkExperienceVO> result = resumeService.getWorkExperiencesByResumeId(resumeId);
        return ApiResponse.success(result);
    }

    /**
     * 添加工作经历
     * POST /api/resume/{resumeId}/work
     */
    @PostMapping("/{resumeId}/work")
    public ApiResponse<WorkExperienceVO> addWorkExperience(@PathVariable Long resumeId, @Valid @RequestBody WorkExperienceDTO dto) {
        WorkExperienceVO result = resumeService.addWorkExperience(resumeId, dto);
        return ApiResponse.success("工作经历添加成功", result);
    }

    /**
     * 更新工作经历
     * PUT /api/resume/work/{workExperienceId}
     */
    @PutMapping("/work/{workExperienceId}")
    public ApiResponse<Void> updateWorkExperience(@PathVariable Long workExperienceId, @Valid @RequestBody WorkExperienceDTO dto) {
        resumeService.updateWorkExperience(workExperienceId, dto);
        return ApiResponse.success("工作经历更新成功", null);
    }

    /**
     * 删除工作经历
     * DELETE /api/resume/work/{workExperienceId}
     */
    @DeleteMapping("/work/{workExperienceId}")
    public ApiResponse<Void> deleteWorkExperience(@PathVariable Long workExperienceId) {
        resumeService.deleteWorkExperience(workExperienceId);
        return ApiResponse.success("工作经历删除成功", null);
    }

    // ==================== 版本管理 ====================

    /**
     * 获取简历版本列表
     * GET /api/resume/versions
     */
    @GetMapping("/versions")
    public ApiResponse<List<ResumeVersionVO>> getVersions(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = UserContext.getCurrentUserId();
        List<ResumeVersionVO> result = resumeService.getVersionsByUserId(userId, limit);
        return ApiResponse.success(result);
    }

    /**
     * 获取版本详情
     * GET /api/resume/versions/{id}
     */
    @GetMapping("/versions/{id}")
    public ApiResponse<ResumeVersionDetailVO> getVersionDetail(@PathVariable Long id) {
        ResumeVersionDetailVO result = resumeService.getVersionDetail(id);
        return ApiResponse.success(result);
    }

    /**
     * 删除版本
     * DELETE /api/resume/versions/{id}
     */
    @DeleteMapping("/versions/{id}")
    public ApiResponse<Void> deleteVersion(@PathVariable Long id) {
        resumeService.deleteVersion(id);
        return ApiResponse.success("版本删除成功", null);
    }
}
