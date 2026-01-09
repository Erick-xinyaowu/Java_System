package com.erickwu.backend.service.impl;

import com.erickwu.backend.entity.Resume;
import com.erickwu.backend.entity.Skill;
import com.erickwu.backend.mapper.*;
import com.erickwu.backend.service.DashboardService;
import com.erickwu.backend.vo.CategoryDataVO;
import com.erickwu.backend.vo.DashboardOverviewVO;
import com.erickwu.backend.vo.RecentActivityVO;
import com.erickwu.backend.vo.TrendDataVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard数据统计服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final ResumeMapper resumeMapper;
    private final SkillMapper skillMapper;
    private final EducationMapper educationMapper;
    private final WorkExperienceMapper workExperienceMapper;

    // 技能等级名称映射
    private static final Map<Integer, String> SKILL_LEVEL_NAMES = Map.of(
            1, "了解",
            2, "熟悉",
            3, "熟练",
            4, "精通",
            5, "专家"
    );

    public DashboardServiceImpl(ResumeMapper resumeMapper,
                                SkillMapper skillMapper,
                                EducationMapper educationMapper,
                                WorkExperienceMapper workExperienceMapper) {
        this.resumeMapper = resumeMapper;
        this.skillMapper = skillMapper;
        this.educationMapper = educationMapper;
        this.workExperienceMapper = workExperienceMapper;
    }

    @Override
    public DashboardOverviewVO getOverview(Long userId) {
        DashboardOverviewVO overview = new DashboardOverviewVO();

        // 获取用户简历
        Resume resume = resumeMapper.findByUserId(userId);

        if (resume != null) {
            Long resumeId = resume.getId();

            // 获取技能列表
            List<Skill> skills = skillMapper.findByResumeId(resumeId);
            overview.setTotalSkills(skills.size());

            // 统计精通及以上的技能（level >= 4）
            long expertCount = skills.stream()
                    .filter(s -> s.getLevel() != null && s.getLevel() >= 4)
                    .count();
            overview.setExpertSkills((int) expertCount);

            // 教育经历数
            int eduCount = educationMapper.countByResumeId(resumeId);
            overview.setEducationCount(eduCount);

            // 工作经历数
            int workCount = workExperienceMapper.countByResumeId(resumeId);
            overview.setWorkExperienceCount(workCount);

            // 计算简历完成度
            int completeness = calculateResumeCompleteness(resume, skills.size(), eduCount, workCount);
            overview.setResumeCompleteness(completeness);

            // 最后更新时间
            overview.setLastUpdateTime(resume.getUpdatedAt());
        } else {
            // 没有简历时的默认值
            overview.setTotalSkills(0);
            overview.setExpertSkills(0);
            overview.setEducationCount(0);
            overview.setWorkExperienceCount(0);
            overview.setResumeCompleteness(0);
            overview.setLastUpdateTime(null);
        }

        // 测评、学习记录、报告等暂时设为0（阶段4、5、6跳过了）
        overview.setAssessmentCount(0);
        overview.setLearningRecordCount(0);
        overview.setTotalLearningHours(0.0);
        overview.setWeeklyLearningHours(0.0);
        overview.setReportCount(0);

        return overview;
    }

    @Override
    public List<TrendDataVO> getLearningTrend(Long userId, String period) {
        // 由于跳过了阶段6学习记录模块，这里返回模拟数据
        List<TrendDataVO> trendData = new ArrayList<>();

        int days;
        switch (period) {
            case "30d":
                days = 30;
                break;
            case "90d":
                days = 90;
                break;
            default:
                days = 7;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        Random random = new Random(userId); // 使用userId作为种子，保证同一用户数据一致

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(formatter);
            // 生成0-4小时的随机学习时长（模拟数据）
            double hours = Math.round(random.nextDouble() * 4 * 10) / 10.0;
            trendData.add(new TrendDataVO(dateStr, hours, "学习时长"));
        }

        return trendData;
    }

    @Override
    public List<CategoryDataVO> getSkillDistribution(Long userId) {
        List<CategoryDataVO> distribution = new ArrayList<>();

        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            return distribution;
        }

        List<Skill> skills = skillMapper.findByResumeId(resume.getId());
        if (skills.isEmpty()) {
            return distribution;
        }

        // 按分类统计技能数量
        Map<String, Long> categoryCount = skills.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCategory() != null ? s.getCategory() : "其他",
                        Collectors.counting()
                ));

        // 计算总数和百分比
        long total = skills.size();
        for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
            double percentage = Math.round(entry.getValue() * 100.0 / total * 10) / 10.0;
            distribution.add(new CategoryDataVO(entry.getKey(), entry.getValue().doubleValue(), percentage));
        }

        // 按数量降序排列
        distribution.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return distribution;
    }

    @Override
    public List<CategoryDataVO> getSkillLevelDistribution(Long userId) {
        List<CategoryDataVO> distribution = new ArrayList<>();

        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            return distribution;
        }

        List<Skill> skills = skillMapper.findByResumeId(resume.getId());
        if (skills.isEmpty()) {
            return distribution;
        }

        // 按等级统计技能数量
        Map<Integer, Long> levelCount = skills.stream()
                .filter(s -> s.getLevel() != null)
                .collect(Collectors.groupingBy(
                        Skill::getLevel,
                        Collectors.counting()
                ));

        // 计算总数和百分比
        long total = skills.stream().filter(s -> s.getLevel() != null).count();

        // 按等级顺序添加（1-5）
        for (int level = 1; level <= 5; level++) {
            long count = levelCount.getOrDefault(level, 0L);
            if (count > 0) {
                String levelName = SKILL_LEVEL_NAMES.getOrDefault(level, "未知");
                double percentage = Math.round(count * 100.0 / total * 10) / 10.0;
                distribution.add(new CategoryDataVO(levelName, (double) count, percentage));
            }
        }

        return distribution;
    }

    @Override
    public List<RecentActivityVO> getRecentActivities(Long userId, int limit) {
        List<RecentActivityVO> activities = new ArrayList<>();

        Resume resume = resumeMapper.findByUserId(userId);
        if (resume == null) {
            return activities;
        }

        // 添加简历更新活动
        if (resume.getUpdatedAt() != null) {
            activities.add(new RecentActivityVO(
                    "resume",
                    "更新简历",
                    "更新了个人简历信息",
                    resume.getUpdatedAt()
            ));
        }

        // 获取最近添加的技能
        List<Skill> skills = skillMapper.findByResumeId(resume.getId());
        for (Skill skill : skills) {
            if (skill.getCreatedAt() != null) {
                activities.add(new RecentActivityVO(
                        "skill",
                        "添加技能",
                        "添加了技能：" + skill.getName(),
                        skill.getCreatedAt()
                ));
            }
        }

        // 按时间降序排列，取前limit条
        activities.sort((a, b) -> {
            if (a.getTime() == null) return 1;
            if (b.getTime() == null) return -1;
            return b.getTime().compareTo(a.getTime());
        });

        if (activities.size() > limit) {
            activities = activities.subList(0, limit);
        }

        return activities;
    }

    /**
     * 计算简历完成度
     */
    private int calculateResumeCompleteness(Resume resume, int skillCount, int eduCount, int workCount) {
        int score = 0;
        int maxScore = 100;

        // 基本信息（30分）
        if (resume.getRealName() != null && !resume.getRealName().isEmpty()) {
            score += 10;
        }
        if (resume.getTitle() != null && !resume.getTitle().isEmpty()) {
            score += 10;
        }
        if (resume.getTargetPosition() != null && !resume.getTargetPosition().isEmpty()) {
            score += 10;
        }

        // 技能（30分）- 至少3个技能得满分
        score += Math.min(skillCount * 10, 30);

        // 教育经历（20分）- 至少1条得满分
        score += Math.min(eduCount * 20, 20);

        // 工作经历（20分）- 至少2条得满分
        score += Math.min(workCount * 10, 20);

        return Math.min(score, maxScore);
    }
}
