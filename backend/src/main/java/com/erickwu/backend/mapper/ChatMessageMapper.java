package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 对话消息 Mapper
 */
@Mapper
public interface ChatMessageMapper {

    @Select("SELECT * FROM chat_message WHERE id = #{id}")
    ChatMessage findById(@Param("id") Long id);

    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<ChatMessage> findBySessionId(@Param("sessionId") Long sessionId);

    @Insert("INSERT INTO chat_message (session_id, role, content, created_at) VALUES (#{sessionId}, #{role}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    @Delete("DELETE FROM chat_message WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") Long sessionId);
}
