package com.erickwu.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 发送消息请求 DTO
 */
public class ChatMessageDTO {
    
    private Long sessionId;  // 可选，如果为空则创建新会话

    @NotBlank(message = "消息内容不能为空")
    private String content;

    // Getters and Setters
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
