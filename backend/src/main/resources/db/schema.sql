-- Active: 1764667029736@@localhost@3307
-- =====================================================
-- Career Planner 数据库初始化脚本
-- 智能职业规划与学业分析系统
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS career_planner 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE career_planner;

-- =====================================================
-- 1. 用户表
-- =====================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` LONGTEXT DEFAULT NULL COMMENT '头像（支持Base64或URL）',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 简历表
-- =====================================================
DROP TABLE IF EXISTS `resume`;
CREATE TABLE `resume` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '简历ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) DEFAULT '我的简历' COMMENT '简历标题',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `target_position` VARCHAR(100) DEFAULT NULL COMMENT '目标职位',
    `expected_salary` VARCHAR(50) DEFAULT NULL COMMENT '期望薪资',
    `work_city` VARCHAR(50) DEFAULT NULL COMMENT '工作城市',
    `education` VARCHAR(50) DEFAULT NULL COMMENT '最高学历',
    `school` VARCHAR(100) DEFAULT NULL COMMENT '毕业院校',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `graduation_year` INT DEFAULT NULL COMMENT '毕业年份',
    `work_experience` INT DEFAULT 0 COMMENT '工作经验（年）',
    `self_introduction` TEXT DEFAULT NULL COMMENT '自我介绍',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_resume_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';

-- =====================================================
-- 3. 技能表
-- =====================================================
DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `name` VARCHAR(50) NOT NULL COMMENT '技能名称',
    `level` TINYINT DEFAULT 3 COMMENT '熟练度：1-了解 2-熟悉 3-掌握 4-精通 5-专家',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '技能分类：编程语言/框架/工具/软技能等',
    `years` INT DEFAULT 0 COMMENT '使用年限',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    KEY `idx_category` (`category`),
    CONSTRAINT `fk_skill_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- =====================================================
-- 4. 工作经历表
-- =====================================================
DROP TABLE IF EXISTS `work_experience`;
CREATE TABLE `work_experience` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '经历ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
    `position` VARCHAR(100) NOT NULL COMMENT '职位',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期（NULL表示至今）',
    `description` TEXT DEFAULT NULL COMMENT '工作描述',
    `achievements` TEXT DEFAULT NULL COMMENT '主要成就',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    CONSTRAINT `fk_work_exp_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作经历表';

-- =====================================================
-- 5. 教育经历表
-- =====================================================
DROP TABLE IF EXISTS `education`;
CREATE TABLE `education` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教育ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `school` VARCHAR(100) NOT NULL COMMENT '学校名称',
    `degree` VARCHAR(50) NOT NULL COMMENT '学位：高中/专科/本科/硕士/博士',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `start_date` DATE NOT NULL COMMENT '入学日期',
    `end_date` DATE DEFAULT NULL COMMENT '毕业日期',
    `gpa` DECIMAL(3,2) DEFAULT NULL COMMENT 'GPA',
    `description` TEXT DEFAULT NULL COMMENT '在校经历',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    CONSTRAINT `fk_edu_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教育经历表';

-- =====================================================
-- 6. 测评记录表
-- =====================================================
DROP TABLE IF EXISTS `assessment`;
CREATE TABLE `assessment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '测评ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(50) NOT NULL COMMENT '测评类型：MBTI/HOLLAND/DISC',
    `result_code` VARCHAR(50) DEFAULT NULL COMMENT '结果代码，如INTJ、IAR等',
    `result_name` VARCHAR(100) DEFAULT NULL COMMENT '结果名称，如"建筑师"',
    `description` TEXT DEFAULT NULL COMMENT '结果描述',
    `strengths` JSON DEFAULT NULL COMMENT '优势列表（JSON数组）',
    `careers` JSON DEFAULT NULL COMMENT '推荐职业（JSON数组）',
    `suggestions` JSON DEFAULT NULL COMMENT '发展建议（JSON数组）',
    `answers` JSON DEFAULT NULL COMMENT '答题记录（JSON对象）',
    `score` INT DEFAULT NULL COMMENT '得分',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    CONSTRAINT `fk_assessment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评记录表';

-- =====================================================
-- 7. 职业报告表
-- =====================================================
DROP TABLE IF EXISTS `career_report`;
CREATE TABLE `career_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '报告标题',
    `type` VARCHAR(50) DEFAULT 'AI生成' COMMENT '报告类型：AI生成/手动创建',
    `content` LONGTEXT NOT NULL COMMENT '报告内容（Markdown格式）',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '报告摘要',
    `based_on` JSON DEFAULT NULL COMMENT '依据数据（关联的测评ID等）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    CONSTRAINT `fk_report_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业报告表';

-- =====================================================
-- 8. 学习记录表
-- =====================================================
DROP TABLE IF EXISTS `learning_record`;
CREATE TABLE `learning_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_name` VARCHAR(200) NOT NULL COMMENT '课程/学习内容名称',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：编程/设计/管理/语言等',
    `platform` VARCHAR(100) DEFAULT NULL COMMENT '学习平台',
    `progress` INT DEFAULT 0 COMMENT '学习进度（百分比）',
    `total_hours` DECIMAL(6,2) DEFAULT 0 COMMENT '总学时',
    `completed_hours` DECIMAL(6,2) DEFAULT 0 COMMENT '已完成学时',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-暂停 1-进行中 2-已完成',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `certificate` VARCHAR(255) DEFAULT NULL COMMENT '证书URL',
    `notes` TEXT DEFAULT NULL COMMENT '学习笔记',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_learning_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习记录表';

-- =====================================================
-- 9. 职业字典表（用于推荐）
-- =====================================================
DROP TABLE IF EXISTS `career_dict`;
CREATE TABLE `career_dict` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '职业ID',
    `name` VARCHAR(100) NOT NULL COMMENT '职业名称',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '职业分类',
    `description` TEXT DEFAULT NULL COMMENT '职业描述',
    `required_skills` JSON DEFAULT NULL COMMENT '所需技能（JSON数组）',
    `salary_range` VARCHAR(50) DEFAULT NULL COMMENT '薪资范围',
    `education_requirement` VARCHAR(50) DEFAULT NULL COMMENT '学历要求',
    `prospects` TEXT DEFAULT NULL COMMENT '发展前景',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业字典表';

-- =====================================================
-- 10. 简历版本表（用于版本管理和分析历史）
-- =====================================================
DROP TABLE IF EXISTS `resume_version`;
CREATE TABLE `resume_version` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `version_number` INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `file_name` VARCHAR(255) DEFAULT NULL COMMENT '上传的文件名',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `raw_text` LONGTEXT DEFAULT NULL COMMENT '原始文本内容',
    `parsed_data` JSON DEFAULT NULL COMMENT '解析后的结构化数据（JSON格式）',
    `analysis_report` LONGTEXT DEFAULT NULL COMMENT 'AI分析报告（Markdown格式）',
    `analysis_metadata` JSON DEFAULT NULL COMMENT '分析元数据（用时、评分等）',
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `version_note` VARCHAR(500) DEFAULT NULL COMMENT '版本备注',
    PRIMARY KEY (`id`),
    KEY `idx_resume_id` (`resume_id`),
    KEY `idx_version_number` (`version_number`),
    KEY `idx_upload_time` (`upload_time`),
    CONSTRAINT `fk_version_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历版本表';
