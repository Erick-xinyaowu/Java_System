package com.erickwu.backend.controller;

import com.erickwu.backend.dto.ChatMessageDTO;
import com.erickwu.backend.service.ChatService;
import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.util.JwtUtil;
import com.erickwu.backend.vo.ChatMessageVO;
import com.erickwu.backend.vo.ChatSessionVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 对话控制器
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    public ChatController(ChatService chatService, JwtUtil jwtUtil) {
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/sessions")
    public ApiResponse<List<ChatSessionVO>> getSessions(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<ChatSessionVO> sessions = chatService.getSessions(userId);
        return ApiResponse.success(sessions);
    }

    /**
     * 获取会话详情（包含消息）
     */
    @GetMapping("/sessions/{id}")
    public ApiResponse<ChatSessionVO> getSessionDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ChatSessionVO session = chatService.getSessionDetail(userId, id);
        return ApiResponse.success(session);
    }

    /**
     * 创建新会话
     */
    @PostMapping("/sessions")
    public ApiResponse<ChatSessionVO> createSession(HttpServletRequest request) {
        Long userId = getUserId(request);
        ChatSessionVO session = chatService.createSession(userId);
        return ApiResponse.success(session);
    }

    /**
     * 发送消息并获取 AI 回复
     */
    @PostMapping("/message")
    public ApiResponse<ChatMessageVO> sendMessage(
            @RequestBody ChatMessageDTO dto,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        ChatMessageVO response = chatService.sendMessage(userId, dto);
        return ApiResponse.success(response);
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/sessions/{id}/title")
    public ApiResponse<Void> updateSessionTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        String title = body.get("title");
        chatService.updateSessionTitle(userId, id, title);
        return ApiResponse.success(null);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/sessions/{id}")
    public ApiResponse<Void> deleteSession(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        chatService.deleteSession(userId, id);
        return ApiResponse.success(null);
    }

    /**
     * 清空所有会话
     */
    @DeleteMapping("/sessions")
    public ApiResponse<Void> clearAllSessions(HttpServletRequest request) {
        Long userId = getUserId(request);
        chatService.clearAllSessions(userId);
        return ApiResponse.success(null);
    }

    /**
     * 从请求中获取用户 ID
     */
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
