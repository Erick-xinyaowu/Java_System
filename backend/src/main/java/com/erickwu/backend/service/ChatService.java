package com.erickwu.backend.service;

import com.erickwu.backend.dto.ChatMessageDTO;
import com.erickwu.backend.vo.ChatMessageVO;
import com.erickwu.backend.vo.ChatSessionVO;

import java.util.List;

/**
 * AI 对话服务接口
 */
public interface ChatService {

    /**
     * 获取用户的所有对话会话
     */
    List<ChatSessionVO> getSessions(Long userId);

    /**
     * 获取会话详情（包含消息）
     */
    ChatSessionVO getSessionDetail(Long userId, Long sessionId);

    /**
     * 创建新会话
     */
    ChatSessionVO createSession(Long userId);

    /**
     * 发送消息并获取 AI 回复
     */
    ChatMessageVO sendMessage(Long userId, ChatMessageDTO dto);

    /**
     * 更新会话标题
     */
    void updateSessionTitle(Long userId, Long sessionId, String title);

    /**
     * 删除会话
     */
    void deleteSession(Long userId, Long sessionId);

    /**
     * 清空所有会话
     */
    void clearAllSessions(Long userId);
}
