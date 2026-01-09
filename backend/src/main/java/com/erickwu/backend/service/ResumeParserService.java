package com.erickwu.backend.service;

import com.erickwu.backend.vo.ResumeParseResultVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 简历解析服务
 * 使用 Apache Tika 提取文档文本，通过通义千问进行智能解析
 */
@Service
public class ResumeParserService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeParserService.class);

    private final QwenLlmService llmService;
    private final ObjectMapper objectMapper;
    private final Tika tika;

    /**
     * 简历解析的系统提示词
     */
    private static final String RESUME_PARSE_SYSTEM_PROMPT = """
            你是一个专业的简历解析助手。请分析给定的简历文本，提取关键信息并以JSON格式返回。
            
            请严格按照以下JSON格式输出，不要包含任何其他文字说明：
            {
                "candidateName": "姓名",
                "contactInfo": {
                    "phone": "手机号",
                    "email": "邮箱",
                    "address": "地址"
                },
                "targetPosition": "目标职位",
                "summary": "个人简介/自我评价",
                "skills": [
                    {
                        "name": "技能名称",
                        "level": 1-5的数字(1了解,2熟悉,3掌握,4精通,5专家),
                        "category": "分类(编程语言/框架/数据库/工具/其他)",
                        "years": 使用年限(数字)
                    }
                ],
                "education": [
                    {
                        "school": "学校名称",
                        "degree": "学位(本科/硕士/博士/专科/高中)",
                        "major": "专业",
                        "startDate": "入学日期(YYYY-MM格式)",
                        "endDate": "毕业日期(YYYY-MM格式)",
                        "gpa": GPA数值(可选),
                        "description": "在校经历描述"
                    }
                ],
                "workExperience": [
                    {
                        "company": "公司名称",
                        "position": "职位",
                        "department": "部门(可选)",
                        "startDate": "开始日期(YYYY-MM格式)",
                        "endDate": "结束日期(YYYY-MM格式,如果是至今则为null)",
                        "description": "工作描述",
                        "achievements": "主要成就"
                    }
                ],
                "projects": [
                    {
                        "name": "项目名称",
                        "role": "担任角色",
                        "startDate": "开始日期",
                        "endDate": "结束日期",
                        "description": "项目描述",
                        "technologies": ["使用的技术"]
                    }
                ]
            }
            
            注意：
            1. 技能等级判断标准：了解=1, 熟悉=2, 掌握=3, 精通=4, 专家=5
            2. 日期格式统一为 YYYY-MM
            3. 如果某个字段在简历中没有提及，使用null
            4. 只返回JSON，不要有任何额外说明文字
            """;

    public ResumeParserService(QwenLlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
        this.tika = new Tika();
    }

    /**
     * 解析简历文件
     *
     * @param file 上传的简历文件
     * @return 解析结果
     */
    public ResumeParseResultVO parseResume(MultipartFile file) throws IOException {
        // 1. 提取文本内容
        String rawText = extractText(file);
        logger.info("从文件 {} 提取到 {} 个字符", file.getOriginalFilename(), rawText.length());

        // 2. 调用 LLM 解析
        String llmResponse = llmService.chat(RESUME_PARSE_SYSTEM_PROMPT, 
                "请解析以下简历内容：\n\n" + rawText);
        logger.debug("LLM 解析响应: {}", llmResponse);

        // 3. 解析 JSON 响应
        ResumeParseResultVO result = parseJsonResponse(llmResponse);
        result.setRawText(rawText);
        result.setFileName(file.getOriginalFilename());
        result.setFileSize(file.getSize());

        return result;
    }

    /**
     * 直接解析文本内容
     *
     * @param text 简历文本
     * @return 解析结果
     */
    public ResumeParseResultVO parseText(String text) {
        String llmResponse = llmService.chat(RESUME_PARSE_SYSTEM_PROMPT, 
                "请解析以下简历内容：\n\n" + text);
        
        ResumeParseResultVO result = parseJsonResponse(llmResponse);
        result.setRawText(text);
        return result;
    }

    /**
     * 使用 Tika 提取文档文本
     */
    private String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        logger.info("解析文件: {}, 类型: {}", filename, contentType);

        // TXT 文件直接读取
        if (filename != null && filename.toLowerCase().endsWith(".txt")) {
            return new String(file.getBytes(), "UTF-8");
        }

        // 其他格式使用 Tika 解析
        try (InputStream is = file.getInputStream()) {
            return tika.parseToString(is);
        } catch (TikaException e) {
            logger.error("Tika 解析文件失败", e);
            throw new IOException("无法解析文件内容: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 LLM 返回的 JSON 响应
     */
    private ResumeParseResultVO parseJsonResponse(String jsonResponse) {
        try {
            // 清理可能的 markdown 代码块标记
            String cleanJson = cleanJsonString(jsonResponse);
            
            JsonNode root = objectMapper.readTree(cleanJson);
            ResumeParseResultVO result = new ResumeParseResultVO();

            // 基本信息
            result.setCandidateName(getTextOrNull(root, "candidateName"));
            result.setTargetPosition(getTextOrNull(root, "targetPosition"));
            result.setSummary(getTextOrNull(root, "summary"));

            // 联系方式
            JsonNode contactInfo = root.path("contactInfo");
            if (!contactInfo.isMissingNode()) {
                result.setPhone(getTextOrNull(contactInfo, "phone"));
                result.setEmail(getTextOrNull(contactInfo, "email"));
                result.setAddress(getTextOrNull(contactInfo, "address"));
            }

            // 技能列表
            result.setSkills(parseSkills(root.path("skills")));

            // 教育经历
            result.setEducations(parseEducations(root.path("education")));

            // 工作经历
            result.setWorkExperiences(parseWorkExperiences(root.path("workExperience")));

            // 存储原始 JSON
            result.setParsedJson(cleanJson);

            return result;
        } catch (Exception e) {
            logger.error("解析 JSON 响应失败: {}", jsonResponse, e);
            // 返回一个基本结果，避免完全失败
            ResumeParseResultVO result = new ResumeParseResultVO();
            result.setParsedJson(jsonResponse);
            return result;
        }
    }

    /**
     * 清理 JSON 字符串（移除 markdown 代码块标记等）
     */
    private String cleanJsonString(String json) {
        String cleaned = json.trim();
        // 移除 markdown 代码块
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asText();
    }

    private List<ResumeParseResultVO.SkillInfo> parseSkills(JsonNode skillsNode) {
        List<ResumeParseResultVO.SkillInfo> skills = new ArrayList<>();
        if (skillsNode.isArray()) {
            for (JsonNode node : skillsNode) {
                ResumeParseResultVO.SkillInfo skill = new ResumeParseResultVO.SkillInfo();
                skill.setName(getTextOrNull(node, "name"));
                skill.setLevel(node.path("level").asInt(3));
                skill.setCategory(getTextOrNull(node, "category"));
                skill.setYears(node.path("years").asInt(0));
                skills.add(skill);
            }
        }
        return skills;
    }

    private List<ResumeParseResultVO.EducationInfo> parseEducations(JsonNode educationsNode) {
        List<ResumeParseResultVO.EducationInfo> educations = new ArrayList<>();
        if (educationsNode.isArray()) {
            for (JsonNode node : educationsNode) {
                ResumeParseResultVO.EducationInfo edu = new ResumeParseResultVO.EducationInfo();
                edu.setSchool(getTextOrNull(node, "school"));
                edu.setDegree(getTextOrNull(node, "degree"));
                edu.setMajor(getTextOrNull(node, "major"));
                edu.setStartDate(parseDate(getTextOrNull(node, "startDate")));
                edu.setEndDate(parseDate(getTextOrNull(node, "endDate")));
                String gpaStr = getTextOrNull(node, "gpa");
                if (gpaStr != null && !gpaStr.isEmpty()) {
                    try {
                        edu.setGpa(new BigDecimal(gpaStr));
                    } catch (NumberFormatException ignored) {}
                }
                edu.setDescription(getTextOrNull(node, "description"));
                educations.add(edu);
            }
        }
        return educations;
    }

    private List<ResumeParseResultVO.WorkExperienceInfo> parseWorkExperiences(JsonNode workExpNode) {
        List<ResumeParseResultVO.WorkExperienceInfo> experiences = new ArrayList<>();
        if (workExpNode.isArray()) {
            for (JsonNode node : workExpNode) {
                ResumeParseResultVO.WorkExperienceInfo exp = new ResumeParseResultVO.WorkExperienceInfo();
                exp.setCompany(getTextOrNull(node, "company"));
                exp.setPosition(getTextOrNull(node, "position"));
                exp.setDepartment(getTextOrNull(node, "department"));
                exp.setStartDate(parseDate(getTextOrNull(node, "startDate")));
                exp.setEndDate(parseDate(getTextOrNull(node, "endDate")));
                exp.setDescription(getTextOrNull(node, "description"));
                exp.setAchievements(getTextOrNull(node, "achievements"));
                experiences.add(exp);
            }
        }
        return experiences;
    }

    /**
     * 解析日期字符串
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }
        try {
            // 尝试 YYYY-MM 格式
            if (dateStr.matches("\\d{4}-\\d{2}")) {
                return LocalDate.parse(dateStr + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            }
            // 尝试 YYYY.MM 格式
            if (dateStr.matches("\\d{4}\\.\\d{2}")) {
                String normalized = dateStr.replace(".", "-");
                return LocalDate.parse(normalized + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            }
            // 尝试完整日期格式
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            logger.warn("无法解析日期: {}", dateStr);
            return null;
        }
    }

    /**
     * 生成简历智能分析报告的系统提示词
     */
    private static final String RESUME_ANALYSIS_SYSTEM_PROMPT = """
            你是一个专业的职业规划顾问和简历分析专家。请根据提供的简历信息，生成一份详细的智能分析报告。

            报告必须使用Markdown格式，包含以下章节：

            # 🧾 简历智能分析报告

            ## 一、候选人概况
            - 姓名、联系方式
            - 当前状态（在校生/在职/待业等）
            - 核心竞争力总结（2-3句话）

            ## 二、教育背景分析
            - 学历层次及学校评价
            - 专业匹配度分析
            - 学业成绩评估
            - 教育亮点总结

            ## 三、技能与能力画像
            ### 3.1 技术技能
            - 主要技术栈
            - 技能广度与深度评估
            - 技术趋势匹配度

            ### 3.2 软技能
            - 团队协作能力
            - 沟通表达能力
            - 领导力与项目管理能力
            - 学习能力与适应性

            ### 3.3 技能评级
            用表格展示各项技能的星级评分（1-5星）

            ## 四、实践与项目经验
            ### 4.1 工作/实习经历
            对每段经历进行分析：职责、价值与成长、成果评估

            ### 4.2 项目经验
            对每个项目分析：复杂度、技术难点与解决方案、个人贡献度

            ### 4.3 竞赛与荣誉
            竞赛成果和荣誉含金量分析

            ## 五、职业发展建议
            ### 5.1 优势分析
            列出3-5个核心优势

            ### 5.2 待提升领域
            列出3-5个需要提升的方面

            ### 5.3 职业方向建议
            - 推荐职业方向（2-3个）
            - 短期（6个月）发展建议
            - 中期（1-2年）发展规划
            - 长期（3-5年）职业目标

            ### 5.4 技能提升路径
            具体的学习建议和资源推荐

            ## 六、综合评价
            ### 6.1 整体评分
            - 技术能力: ⭐⭐⭐⭐☆ (x/5)
            - 项目经验: ⭐⭐⭐☆☆ (x/5)
            - 发展潜力: ⭐⭐⭐⭐☆ (x/5)
            - 综合评分: ⭐⭐⭐⭐☆ (x/5)

            ### 6.2 推荐指数
            ✅ 推荐录用 / ⚠️ 有保留推荐 / ❌ 暂不推荐
            并给出具体理由

            ### 6.3 一句话总结
            用一句话概括候选人特点

            注意事项：
            1. 分析要客观、专业、有建设性
            2. 评价要具体，避免空泛
            3. 建议要可执行、有针对性
            4. 格式要清晰、美观
            """;

    /**
     * 生成简历智能分析报告
     *
     * @param parseResult 解析后的简历数据
     * @return 分析报告（Markdown格式）
     */
    public String generateAnalysisReport(ResumeParseResultVO parseResult) {
        // 构建简历摘要用于分析
        StringBuilder resumeSummary = new StringBuilder();
        resumeSummary.append("候选人姓名：").append(parseResult.getCandidateName()).append("\n");
        resumeSummary.append("目标职位：").append(parseResult.getTargetPosition()).append("\n");
        resumeSummary.append("个人简介：").append(parseResult.getSummary()).append("\n\n");

        // 技能信息
        resumeSummary.append("技能列表：\n");
        if (parseResult.getSkills() != null) {
            for (var skill : parseResult.getSkills()) {
                String levelStr = switch (skill.getLevel()) {
                    case 1 -> "了解";
                    case 2 -> "熟悉";
                    case 3 -> "掌握";
                    case 4 -> "精通";
                    case 5 -> "专家";
                    default -> "未知";
                };
                resumeSummary.append("- ").append(skill.getName())
                        .append(" (").append(levelStr).append(", ")
                        .append(skill.getYears()).append("年经验, ")
                        .append(skill.getCategory()).append(")\n");
            }
        }

        // 教育经历
        resumeSummary.append("\n教育经历：\n");
        if (parseResult.getEducations() != null) {
            for (var edu : parseResult.getEducations()) {
                resumeSummary.append("- ").append(edu.getSchool())
                        .append(", ").append(edu.getMajor())
                        .append(", ").append(edu.getDegree())
                        .append(" (").append(edu.getStartDate()).append(" - ").append(edu.getEndDate()).append(")\n");
                if (edu.getDescription() != null) {
                    resumeSummary.append("  描述：").append(edu.getDescription()).append("\n");
                }
            }
        }

        // 工作经历
        resumeSummary.append("\n工作经历：\n");
        if (parseResult.getWorkExperiences() != null) {
            for (var work : parseResult.getWorkExperiences()) {
                resumeSummary.append("- ").append(work.getCompany())
                        .append(" - ").append(work.getPosition())
                        .append(" (").append(work.getStartDate()).append(" - ")
                        .append(work.getEndDate() != null ? work.getEndDate() : "至今").append(")\n");
                if (work.getDescription() != null) {
                    resumeSummary.append("  职责：").append(work.getDescription()).append("\n");
                }
                if (work.getAchievements() != null) {
                    resumeSummary.append("  成就：").append(work.getAchievements()).append("\n");
                }
            }
        }

        // 原始简历文本
        resumeSummary.append("\n原始简历内容：\n").append(parseResult.getRawText());

        logger.info("开始生成简历分析报告...");
        String report = llmService.chat(RESUME_ANALYSIS_SYSTEM_PROMPT,
                "请根据以下简历信息生成智能分析报告：\n\n" + resumeSummary.toString());
        logger.info("简历分析报告生成完成");

        return report;
    }
}
