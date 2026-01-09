package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.CareerReport;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 职业报告 Mapper 接口
 */
@Mapper
public interface CareerReportMapper {

    /**
     * 根据ID查询报告
     */
    @Select("SELECT * FROM career_report WHERE id = #{id}")
    CareerReport findById(@Param("id") Long id);

    /**
     * 根据用户ID查询报告列表
     */
    @Select("SELECT * FROM career_report WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<CareerReport> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和类型查询报告
     */
    @Select("SELECT * FROM career_report WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    List<CareerReport> findByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 查询用户最新的报告
     */
    @Select("SELECT * FROM career_report WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
    CareerReport findLatestByUserId(@Param("userId") Long userId);

    /**
     * 插入报告
     */
    @Insert("INSERT INTO career_report (user_id, title, type, content, summary, based_on) " +
            "VALUES (#{userId}, #{title}, #{type}, #{content}, #{summary}, #{basedOn})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CareerReport report);

    /**
     * 更新报告
     */
    @Update("UPDATE career_report SET title = #{title}, type = #{type}, content = #{content}, " +
            "summary = #{summary}, based_on = #{basedOn}, updated_at = NOW() WHERE id = #{id}")
    int update(CareerReport report);

    /**
     * 删除报告
     */
    @Delete("DELETE FROM career_report WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据用户ID删除所有报告
     */
    @Delete("DELETE FROM career_report WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 统计用户报告数量
     */
    @Select("SELECT COUNT(*) FROM career_report WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
