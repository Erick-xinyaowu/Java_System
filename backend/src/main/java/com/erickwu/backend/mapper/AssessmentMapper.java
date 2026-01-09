package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.Assessment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 测评记录 Mapper 接口
 */
@Mapper
public interface AssessmentMapper {

    /**
     * 根据ID查询测评
     */
    @Select("SELECT * FROM assessment WHERE id = #{id}")
    Assessment findById(@Param("id") Long id);

    /**
     * 根据用户ID查询测评列表
     */
    @Select("SELECT * FROM assessment WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Assessment> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和类型查询测评
     */
    @Select("SELECT * FROM assessment WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    List<Assessment> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 查询用户最新的测评
     */
    @Select("SELECT * FROM assessment WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
    Assessment findLatestByUserId(@Param("userId") Long userId);

    /**
     * 查询用户某类型最新的测评
     */
    @Select("SELECT * FROM assessment WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC LIMIT 1")
    Assessment findLatestByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 插入测评记录
     */
    @Insert("INSERT INTO assessment (user_id, type, result_code, result_name, description, " +
            "strengths, careers, suggestions, answers, score) " +
            "VALUES (#{userId}, #{type}, #{resultCode}, #{resultName}, #{description}, " +
            "#{strengths}, #{careers}, #{suggestions}, #{answers}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Assessment assessment);

    /**
     * 更新测评记录
     */
    @Update("UPDATE assessment SET result_code = #{resultCode}, result_name = #{resultName}, " +
            "description = #{description}, strengths = #{strengths}, careers = #{careers}, " +
            "suggestions = #{suggestions}, answers = #{answers}, score = #{score} WHERE id = #{id}")
    int update(Assessment assessment);

    /**
     * 删除测评记录
     */
    @Delete("DELETE FROM assessment WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据用户ID删除所有测评记录
     */
    @Delete("DELETE FROM assessment WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 统计用户测评次数
     */
    @Select("SELECT COUNT(*) FROM assessment WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户某类型测评次数
     */
    @Select("SELECT COUNT(*) FROM assessment WHERE user_id = #{userId} AND type = #{type}")
    int countByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);
}
