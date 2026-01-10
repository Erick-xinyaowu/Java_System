package com.erickwu.backend.service.impl;

import com.erickwu.backend.dto.*;
import com.erickwu.backend.entity.*;
import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.model.ErrorCode;
import com.erickwu.backend.mapper.*;
import com.erickwu.backend.service.ResumeParserService;
import com.erickwu.backend.service.ResumeService;
import com.erickwu.backend.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历服务实现类
 */
@Service
@SuppressWarnings("null") // 抑制 BeanUtils.copyProperties 的 null 安全警告
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final SkillMapper skillMapper;
    private final EducationMapper educationMapper;
    private final WorkExperienceMapper workExperienceMapper;
    private final ResumeVersionMapper resumeVersionMapper;
    private final ResumeParserService resumeParserService;

    public ResumeServiceImpl(ResumeMapper resumeMapper,
                             SkillMapper skillMapper,
                             EducationMapper educationMapper,
                             WorkExperienceMapper workExperienceMapper,
                             ResumeVersionMapper resumeVersionMapper,
                             ResumeParserService resumeParserService) {
        this.resumeMapper = resumeMapper;
        this.skillMapper = skillMapper;
        this.educationMapper = educationMapper;
        this.workExperienceMapper = workExperienceMapper;
        this.resumeVersionMapper = resumeVersionMapper;
        this.resumeParserService = resumeParserService;
    }

    // ==================== 简历基本操作 ====================

    @Override
    @Transactional
    public ResumeDetailVO createResume(Long userId, ResumeDTO dto) {
        // 检查用户是否已有简历
        Resume existingResume = resumeMapper.findByUserId(userId);
        if (existingResume != null) {
            throw new BusinessException(ErrorCode.RESUME_ALREADY_EXISTS, "用户已有简历，不能重复创建");
        }

        // 创建简历
        Resume resume = new Resume();
        resume.setUserId(userId);
        BeanUtils.copyProperties(dto, resume);
        resumeMapper.insert(resume);

        return getResumeDetail(resume.getId());
    }

    @Override
    public ResumeDetailVO getResumeDetail(Long resumeId) {
        Resume resume = resumeMapper.findById(resumeId);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        return convertToDetailVO(resume);
    }

    @Override
    public ResumeDetailVO getResumeByUserId(Long userId) {
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            return null;
        }
        return convertToDetailVO(resume);
    }

    @Override
    @Transactional
    public void updateResume(Long resumeId, ResumeDTO dto) {
        Resume resume = resumeMapper.findById(resumeId);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, resume);
        resume.setId(resumeId);
        resumeMapper.update(resume);
    }

    @Override
    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = resumeMapper.findById(resumeId);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        // 删除关联的技能、教育经历、工作经历
        skillMapper.deleteByResumeId(resumeId);
        educationMapper.deleteByResumeId(resumeId);
        workExperienceMapper.deleteByResumeId(resumeId);

        // 删除简历
        resumeMapper.delete(resumeId);
    }

    // ==================== 技能管理 ====================

    @Override
    @Transactional
    public SkillVO addSkill(Long resumeId, SkillDTO dto) {
        checkResumeExists(resumeId);

        Skill skill = new Skill();
        skill.setResumeId(resumeId);
        BeanUtils.copyProperties(dto, skill);
        skillMapper.insert(skill);

        return convertToSkillVO(skill);
    }

    @Override
    @Transactional
    public List<SkillVO> batchAddSkills(Long resumeId, List<SkillDTO> dtoList) {
        checkResumeExists(resumeId);

        List<Skill> skills = dtoList.stream()
                .map(dto -> {
                    Skill skill = new Skill();
                    skill.setResumeId(resumeId);
                    BeanUtils.copyProperties(dto, skill);
                    return skill;
                })
                .collect(Collectors.toList());

        skillMapper.batchInsert(skills);

        // 重新查询以获取ID
        return skillMapper.findByResumeId(resumeId).stream()
                .map(this::convertToSkillVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSkill(Long skillId, SkillDTO dto) {
        Skill skill = skillMapper.findById(skillId);
        if (skill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "技能不存在");
        }

        BeanUtils.copyProperties(dto, skill);
        skill.setId(skillId);
        skillMapper.update(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long skillId) {
        Skill skill = skillMapper.findById(skillId);
        if (skill == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "技能不存在");
        }
        skillMapper.delete(skillId);
    }

    @Override
    public List<SkillVO> getSkillsByResumeId(Long resumeId) {
        return skillMapper.findByResumeId(resumeId).stream()
                .map(this::convertToSkillVO)
                .collect(Collectors.toList());
    }

    // ==================== 教育经历管理 ====================

    @Override
    @Transactional
    public EducationVO addEducation(Long resumeId, EducationDTO dto) {
        checkResumeExists(resumeId);

        Education education = new Education();
        education.setResumeId(resumeId);
        BeanUtils.copyProperties(dto, education);
        educationMapper.insert(education);

        return convertToEducationVO(education);
    }

    @Override
    @Transactional
    public void updateEducation(Long educationId, EducationDTO dto) {
        Education education = educationMapper.findById(educationId);
        if (education == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教育经历不存在");
        }

        BeanUtils.copyProperties(dto, education);
        education.setId(educationId);
        educationMapper.update(education);
    }

    @Override
    @Transactional
    public void deleteEducation(Long educationId) {
        Education education = educationMapper.findById(educationId);
        if (education == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教育经历不存在");
        }
        educationMapper.delete(educationId);
    }

    @Override
    public List<EducationVO> getEducationsByResumeId(Long resumeId) {
        return educationMapper.findByResumeId(resumeId).stream()
                .map(this::convertToEducationVO)
                .collect(Collectors.toList());
    }

    // ==================== 工作经历管理 ====================

    @Override
    @Transactional
    public WorkExperienceVO addWorkExperience(Long resumeId, WorkExperienceDTO dto) {
        checkResumeExists(resumeId);

        WorkExperience workExp = new WorkExperience();
        workExp.setResumeId(resumeId);
        BeanUtils.copyProperties(dto, workExp);
        workExperienceMapper.insert(workExp);

        return convertToWorkExperienceVO(workExp);
    }

    @Override
    @Transactional
    public void updateWorkExperience(Long workExperienceId, WorkExperienceDTO dto) {
        WorkExperience workExp = workExperienceMapper.findById(workExperienceId);
        if (workExp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "工作经历不存在");
        }

        BeanUtils.copyProperties(dto, workExp);
        workExp.setId(workExperienceId);
        workExperienceMapper.update(workExp);
    }

    @Override
    @Transactional
    public void deleteWorkExperience(Long workExperienceId) {
        WorkExperience workExp = workExperienceMapper.findById(workExperienceId);
        if (workExp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "工作经历不存在");
        }
        workExperienceMapper.delete(workExperienceId);
    }

    @Override
    public List<WorkExperienceVO> getWorkExperiencesByResumeId(Long resumeId) {
        return workExperienceMapper.findByResumeId(resumeId).stream()
                .map(this::convertToWorkExperienceVO)
                .collect(Collectors.toList());
    }

    // ==================== 版本管理 ====================

    @Override
    public List<ResumeVersionVO> getVersionsByUserId(Long userId, int limit) {
        // 先获取用户的简历
        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            return new ArrayList<>();
        }

        List<ResumeVersion> versions = resumeVersionMapper.findByResumeId(resume.getId(), limit);

        return versions.stream()
                .map(v -> convertToVersionVO(v, resume.getRealName()))
                .collect(Collectors.toList());
    }

    @Override
    public ResumeVersionDetailVO getVersionDetail(Long versionId) {
        ResumeVersion version = resumeVersionMapper.findById(versionId);
        if (version == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "版本不存在");
        }

        Resume resume = resumeMapper.findById(version.getResumeId());
        return convertToVersionDetailVO(version, resume != null ? resume.getRealName() : null);
    }

    @Override
    @Transactional
    public void deleteVersion(Long versionId) {
        ResumeVersion version = resumeVersionMapper.findById(versionId);
        if (version == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "版本不存在");
        }
        resumeVersionMapper.delete(versionId);
    }

    // ==================== 私有辅助方法 ====================

    private void checkResumeExists(Long resumeId) {
        Resume resume = resumeMapper.findById(resumeId);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }
    }

    /**
     * 将 Resume 转换为 ResumeDetailVO
     */
    private ResumeDetailVO convertToDetailVO(Resume resume) {
        ResumeDetailVO vo = new ResumeDetailVO();
        BeanUtils.copyProperties(resume, vo);

        // 获取关联的技能列表
        List<Skill> skills = skillMapper.findByResumeId(resume.getId());
        vo.setSkills(skills.stream()
                .map(this::convertToSkillVO)
                .collect(Collectors.toList()));

        // 获取关联的教育经历
        List<Education> educations = educationMapper.findByResumeId(resume.getId());
        vo.setEducations(educations.stream()
                .map(this::convertToEducationVO)
                .collect(Collectors.toList()));

        // 获取关联的工作经历
        List<WorkExperience> workExperiences = workExperienceMapper.findByResumeId(resume.getId());
        vo.setWorkExperiences(workExperiences.stream()
                .map(this::convertToWorkExperienceVO)
                .collect(Collectors.toList()));

        return vo;
    }

    /**
     * 将 Skill 转换为 SkillVO
     */
    private SkillVO convertToSkillVO(Skill skill) {
        SkillVO vo = new SkillVO();
        BeanUtils.copyProperties(skill, vo);
        return vo;
    }

    /**
     * 将 Education 转换为 EducationVO
     */
    private EducationVO convertToEducationVO(Education education) {
        EducationVO vo = new EducationVO();
        BeanUtils.copyProperties(education, vo);
        return vo;
    }

    /**
     * 将 WorkExperience 转换为 WorkExperienceVO
     */
    private WorkExperienceVO convertToWorkExperienceVO(WorkExperience workExp) {
        WorkExperienceVO vo = new WorkExperienceVO();
        BeanUtils.copyProperties(workExp, vo);
        return vo;
    }

    /**
     * 将 ResumeVersion 转换为 ResumeVersionVO
     */
    private ResumeVersionVO convertToVersionVO(ResumeVersion version, String candidateName) {
        ResumeVersionVO vo = new ResumeVersionVO();
        vo.setId(version.getId());
        vo.setResumeId(version.getResumeId());
        vo.setVersionNumber(version.getVersionNumber());
        vo.setFileName(version.getFileName());
        vo.setFileSize(version.getFileSize());
        vo.setUploadTime(version.getUploadTime());
        vo.setVersionNote(version.getVersionNote());
        vo.setCandidateName(candidateName);
        vo.setHasAnalysis(version.getAnalysisReport() != null && !version.getAnalysisReport().isEmpty());
        return vo;
    }

    /**
     * 将 ResumeVersion 转换为 ResumeVersionDetailVO
     */
    private ResumeVersionDetailVO convertToVersionDetailVO(ResumeVersion version, String candidateName) {
        ResumeVersionDetailVO vo = new ResumeVersionDetailVO();
        BeanUtils.copyProperties(version, vo);
        vo.setCandidateName(candidateName);
        // hasAnalysis 是计算属性，不需要单独设置
        return vo;
    }

    // ==================== 简历上传解析 ====================

    @Override
    @Transactional
    public ResumeParseResultVO uploadAndParseResume(Long userId, MultipartFile file, String versionNote) throws IOException {
        // 1. 解析简历文件
        ResumeParseResultVO parseResult = resumeParserService.parseResume(file);

        // 2. 获取或创建简历
        Resume resume = resumeMapper.findByUserId(userId);
        boolean isNewResume = (resume == null);
        
        if (isNewResume) {
            // 创建新简历
            resume = new Resume();
            resume.setUserId(userId);
            resume.setTitle("我的简历");
            resume.setRealName(parseResult.getCandidateName());
            resume.setTargetPosition(parseResult.getTargetPosition());
            resume.setSelfIntroduction(parseResult.getSummary());
            resumeMapper.insert(resume);
        }

        // 3. 生成智能分析报告
        String analysisReport = resumeParserService.generateAnalysisReport(parseResult);

        // 4. 创建版本记录
        Integer maxVersion = resumeVersionMapper.getMaxVersionNumber(resume.getId());
        int newVersionNumber = (maxVersion == null ? 0 : maxVersion) + 1;

        ResumeVersion version = new ResumeVersion();
        version.setResumeId(resume.getId());
        version.setVersionNumber(newVersionNumber);
        version.setFileName(file.getOriginalFilename());
        version.setFileSize(file.getSize());
        version.setRawText(parseResult.getRawText());
        version.setParsedData(parseResult.getParsedJson());
        version.setAnalysisReport(analysisReport);  // 保存分析报告
        version.setUploadTime(LocalDateTime.now());
        version.setVersionNote(versionNote);
        resumeVersionMapper.insert(version);

        // 5. 自动保存技能数据到数据库（用于仪表盘技能分布展示）
        final Long resumeId = resume.getId();
        if (parseResult.getSkills() != null && !parseResult.getSkills().isEmpty()) {
            // 清除旧的技能数据（每次上传新简历都更新技能）
            skillMapper.deleteByResumeId(resumeId);
            
            // 保存新的技能数据
            List<Skill> skills = parseResult.getSkills().stream()
                    .map(s -> {
                        Skill skill = new Skill();
                        skill.setResumeId(resumeId);
                        skill.setName(s.getName());
                        skill.setLevel(s.getLevel());
                        skill.setCategory(s.getCategory());
                        skill.setYears(s.getYears());
                        return skill;
                    })
                    .collect(Collectors.toList());
            skillMapper.batchInsert(skills);
        }

        // 6. 设置版本ID到返回结果，便于前端跳转
        parseResult.setVersionId(version.getId());

        return parseResult;
    }

    @Override
    @Transactional
    public ResumeDetailVO saveParseResult(Long userId, ResumeParseResultVO parseResult) {
        // 获取或创建简历
        Resume resume = resumeMapper.findByUserId(userId);
        boolean isNew = false;

        if (resume == null) {
            isNew = true;
            resume = new Resume();
            resume.setUserId(userId);
        }

        // 更新基本信息
        resume.setTitle("我的简历");
        if (parseResult.getCandidateName() != null) {
            resume.setRealName(parseResult.getCandidateName());
        }
        if (parseResult.getTargetPosition() != null) {
            resume.setTargetPosition(parseResult.getTargetPosition());
        }
        if (parseResult.getSummary() != null) {
            resume.setSelfIntroduction(parseResult.getSummary());
        }

        if (isNew) {
            resumeMapper.insert(resume);
        } else {
            resumeMapper.update(resume);
            // 清除旧的技能、教育、工作经历
            skillMapper.deleteByResumeId(resume.getId());
            educationMapper.deleteByResumeId(resume.getId());
            workExperienceMapper.deleteByResumeId(resume.getId());
        }

        // 获取最终的 resumeId（用于 lambda 表达式）
        final Long resumeId = resume.getId();

        // 保存技能
        if (parseResult.getSkills() != null && !parseResult.getSkills().isEmpty()) {
            List<Skill> skills = parseResult.getSkills().stream()
                    .map(s -> {
                        Skill skill = new Skill();
                        skill.setResumeId(resumeId);
                        skill.setName(s.getName());
                        skill.setLevel(s.getLevel());
                        skill.setCategory(s.getCategory());
                        skill.setYears(s.getYears());
                        return skill;
                    })
                    .collect(Collectors.toList());
            skillMapper.batchInsert(skills);
        }

        // 保存教育经历
        if (parseResult.getEducations() != null && !parseResult.getEducations().isEmpty()) {
            List<Education> educations = parseResult.getEducations().stream()
                    .map(e -> {
                        Education edu = new Education();
                        edu.setResumeId(resumeId);
                        edu.setSchool(e.getSchool());
                        edu.setDegree(e.getDegree());
                        edu.setMajor(e.getMajor());
                        edu.setStartDate(e.getStartDate());
                        edu.setEndDate(e.getEndDate());
                        edu.setGpa(e.getGpa());
                        edu.setDescription(e.getDescription());
                        return edu;
                    })
                    .collect(Collectors.toList());
            educationMapper.batchInsert(educations);
        }

        // 保存工作经历
        if (parseResult.getWorkExperiences() != null && !parseResult.getWorkExperiences().isEmpty()) {
            List<WorkExperience> workExps = parseResult.getWorkExperiences().stream()
                    .map(w -> {
                        WorkExperience exp = new WorkExperience();
                        exp.setResumeId(resumeId);
                        exp.setCompany(w.getCompany());
                        exp.setPosition(w.getPosition());
                        exp.setDepartment(w.getDepartment());
                        exp.setStartDate(w.getStartDate());
                        exp.setEndDate(w.getEndDate());
                        exp.setDescription(w.getDescription());
                        exp.setAchievements(w.getAchievements());
                        return exp;
                    })
                    .collect(Collectors.toList());
            workExperienceMapper.batchInsert(workExps);
        }

        return getResumeDetail(resume.getId());
    }
}
