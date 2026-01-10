package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.ChatSession;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 对话会话 Mapper
 */
@Mapper
public interface ChatSessionMapper {

    @Select("SELECT * FROM chat_session WHERE id = #{id}")
    ChatSession findById(@Param("id") Long id);

    @Select("SELECT * FROM chat_session WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<ChatSession> findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO chat_session (user_id, title, created_at, updated_at) VALUES (#{userId}, #{title}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatSession session);

    @Update("UPDATE chat_session SET title = #{title}, updated_at = NOW() WHERE id = #{id}")
    int updateTitle(@Param("id") Long id, @Param("title") String title);

    @Update("UPDATE chat_session SET updated_at = NOW() WHERE id = #{id}")
    int updateTime(@Param("id") Long id);

    @Delete("DELETE FROM chat_session WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("DELETE FROM chat_session WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
