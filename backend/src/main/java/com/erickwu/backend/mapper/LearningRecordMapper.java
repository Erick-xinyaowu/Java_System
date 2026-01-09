package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.LearningRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 学习记录 Mapper 接口
 */
@Mapper
public interface LearningRecordMapper {

    /**
     * 根据ID查询学习记录
     */
    @Select("SELECT * FROM learning_record WHERE id = #{id}")
    LearningRecord findById(@Param("id") Long id);

    /**
     * 根据用户ID查询学习记录列表
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<LearningRecord> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询学习记录
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} AND status = #{status} ORDER BY updated_at DESC")
    List<LearningRecord> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据用户ID和分类查询学习记录
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} AND category = #{category} ORDER BY updated_at DESC")
    List<LearningRecord> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

    /**
     * 查询进行中的学习记录
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} AND status = 1 ORDER BY updated_at DESC")
    List<LearningRecord> findInProgressByUserId(@Param("userId") Long userId);

    /**
     * 查询已完成的学习记录
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} AND status = 2 ORDER BY end_date DESC")
    List<LearningRecord> findCompletedByUserId(@Param("userId") Long userId);

    /**
     * 插入学习记录
     */
    @Insert("INSERT INTO learning_record (user_id, course_name, category, platform, progress, " +
            "total_hours, completed_hours, status, start_date, end_date, certificate, notes) " +
            "VALUES (#{userId}, #{courseName}, #{category}, #{platform}, #{progress}, " +
            "#{totalHours}, #{completedHours}, #{status}, #{startDate}, #{endDate}, #{certificate}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LearningRecord record);

    /**
     * 更新学习记录
     */
    @Update("UPDATE learning_record SET course_name = #{courseName}, category = #{category}, " +
            "platform = #{platform}, progress = #{progress}, total_hours = #{totalHours}, " +
            "completed_hours = #{completedHours}, status = #{status}, start_date = #{startDate}, " +
            "end_date = #{endDate}, certificate = #{certificate}, notes = #{notes}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int update(LearningRecord record);

    /**
     * 更新学习进度
     */
    @Update("UPDATE learning_record SET progress = #{progress}, completed_hours = #{completedHours}, " +
            "status = CASE WHEN #{progress} >= 100 THEN 2 ELSE status END, " +
            "end_date = CASE WHEN #{progress} >= 100 THEN CURDATE() ELSE end_date END, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress, 
                       @Param("completedHours") java.math.BigDecimal completedHours);

    /**
     * 删除学习记录
     */
    @Delete("DELETE FROM learning_record WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 根据用户ID删除所有学习记录
     */
    @Delete("DELETE FROM learning_record WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 统计用户学习记录数量
     */
    @Select("SELECT COUNT(*) FROM learning_record WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已完成课程数量
     */
    @Select("SELECT COUNT(*) FROM learning_record WHERE user_id = #{userId} AND status = 2")
    int countCompletedByUserId(@Param("userId") Long userId);

    /**
     * 统计用户总学习时长
     */
    @Select("SELECT COALESCE(SUM(completed_hours), 0) FROM learning_record WHERE user_id = #{userId}")
    java.math.BigDecimal sumCompletedHoursByUserId(@Param("userId") Long userId);
}
