package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.Resume;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 简历 Mapper 接口
 */
@Mapper
public interface ResumeMapper {

    /**
     * 根据ID查询简历
     */
    @Select("SELECT * FROM resume WHERE id = #{id}")
    Resume findById(@Param("id") Long id);

    /**
     * 根据用户ID查询简历
     */
    @Select("SELECT * FROM resume WHERE user_id = #{userId}")
    Resume findByUserId(@Param("userId") Long userId);

    /**
     * 查询所有简历
     */
    @Select("SELECT * FROM resume ORDER BY updated_at DESC")
    List<Resume> findAll();

    /**
     * 插入简历
     */
    @Insert("INSERT INTO resume (user_id, title, real_name, target_position, expected_salary, " +
            "work_city, education, school, major, graduation_year, work_experience, self_introduction) " +
            "VALUES (#{userId}, #{title}, #{realName}, #{targetPosition}, #{expectedSalary}, " +
            "#{workCity}, #{education}, #{school}, #{major}, #{graduationYear}, #{workExperience}, #{selfIntroduction})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resume resume);

    /**
     * 更新简历
     */
    @Update("UPDATE resume SET title = #{title}, real_name = #{realName}, target_position = #{targetPosition}, " +
            "expected_salary = #{expectedSalary}, work_city = #{workCity}, education = #{education}, " +
            "school = #{school}, major = #{major}, graduation_year = #{graduationYear}, " +
            "work_experience = #{workExperience}, self_introduction = #{selfIntroduction}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int update(Resume resume);

    /**
     * 删除简历
     */
    @Delete("DELETE FROM resume WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据用户ID删除简历
     */
    @Delete("DELETE FROM resume WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
