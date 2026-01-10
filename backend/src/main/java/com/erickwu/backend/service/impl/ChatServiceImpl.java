package com.erickwu.backend.service.impl;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.dto.ChatMessageDTO;
import com.erickwu.backend.entity.ChatMessage;
import com.erickwu.backend.entity.ChatSession;
import com.erickwu.backend.mapper.ChatMessageMapper;
import com.erickwu.backend.mapper.ChatSessionMapper;
import com.erickwu.backend.service.ChatService;
import com.erickwu.backend.service.QwenLlmService;
import com.erickwu.backend.vo.ChatMessageVO;
import com.erickwu.backend.vo.ChatSessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 对话服务实现
 */
@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatSessionMapper sessionMapper;
    private final ChatMessageMapper messageMapper;
    private final QwenLlmService llmService;

    /**
     * AI 助手系统提示词
     */
    private static final String SYSTEM_PROMPT = """
            你是一个专业的职业规划顾问和求职助手，名叫"小智"。你的职责是帮助用户：
            
            1. **职业规划建议**：根据用户的背景、技能和兴趣，提供个性化的职业发展建议
            2. **简历优化**：帮助用户改进简历内容，突出亮点
            3. **面试准备**：提供面试技巧、常见问题解答和模拟面试
            4. **技能提升**：推荐学习资源和技能提升路径
            5. **行业分析**：提供行业趋势、薪资水平等信息
            6. **求职策略**：帮助制定求职计划和策略
            
            回答要求：
            - 使用友好、专业的语气
            - 回答要具体、实用、可操作
            - 适当使用 Markdown 格式来组织内容
            - 可以使用表格、列表等方式使信息更清晰
            - 如果用户的问题不清楚，可以追问以获取更多信息
            - 鼓励用户，给予积极的反馈
            """;

    public ChatServiceImpl(ChatSessionMapper sessionMapper, 
                          ChatMessageMapper messageMapper,
                          QwenLlmService llmService) {
        this.sessionMapper = sessionMapper;
        this.messageMapper = messageMapper;
        this.llmService = llmService;
    }

    @Override
    public List<ChatSessionVO> getSessions(Long userId) {
        List<ChatSession> sessions = sessionMapper.findByUserId(userId);
        return sessions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatSessionVO getSessionDetail(Long userId, Long sessionId) {
        ChatSession session = sessionMapper.findById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BusinessException(404, "会话不存在");
        }

        ChatSessionVO vo = convertToVO(session);
        List<ChatMessage> messages = messageMapper.findBySessionId(sessionId);
        vo.setMessages(messages.stream()
                .map(this::convertToMessageVO)
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public ChatSessionVO createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle("新对话");
        sessionMapper.insert(session);
        
        ChatSessionVO vo = convertToVO(session);
        vo.setMessages(new ArrayList<>());
        return vo;
    }

    @Override
    @Transactional
    public ChatMessageVO sendMessage(Long userId, ChatMessageDTO dto) {
        Long sessionId = dto.getSessionId();
        
        // 如果没有指定会话，创建新会话
        if (sessionId == null) {
            ChatSession newSession = new ChatSession();
            newSession.setUserId(userId);
            newSession.setTitle("新对话");
            sessionMapper.insert(newSession);
            sessionId = newSession.getId();
        } else {
            // 验证会话归属
            ChatSession session = sessionMapper.findById(sessionId);
            if (session == null || !session.getUserId().equals(userId)) {
                throw new BusinessException(404, "会话不存在");
            }
        }

        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRole("user");
        userMessage.setContent(dto.getContent());
        messageMapper.insert(userMessage);

        // 获取历史消息用于上下文
        List<ChatMessage> history = messageMapper.findBySessionId(sessionId);
        
        // 构建对话历史
        StringBuilder contextBuilder = new StringBuilder();
        // 只保留最近10条消息作为上下文
        int startIdx = Math.max(0, history.size() - 10);
        for (int i = startIdx; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            if ("user".equals(msg.getRole())) {
                contextBuilder.append("用户: ").append(msg.getContent()).append("\n\n");
            } else {
                contextBuilder.append("助手: ").append(msg.getContent()).append("\n\n");
            }
        }

        // 调用 AI 生成回复
        logger.info("开始调用 AI 生成回复，会话ID: {}", sessionId);
        String aiResponse = llmService.chat(SYSTEM_PROMPT, contextBuilder.toString());
        logger.info("AI 回复生成完成");

        // 保存 AI 回复
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(aiResponse);
        messageMapper.insert(assistantMessage);

        // 如果是第一条消息，根据内容更新会话标题
        if (history.size() == 1) {
            String title = generateTitle(dto.getContent());
            sessionMapper.updateTitle(sessionId, title);
        }

        // 更新会话时间
        sessionMapper.updateTime(sessionId);

        // 返回 AI 回复
        ChatMessageVO responseVO = new ChatMessageVO();
        responseVO.setId(assistantMessage.getId());
        responseVO.setRole("assistant");
        responseVO.setContent(aiResponse);
        responseVO.setCreatedAt(assistantMessage.getCreatedAt());
        
        return responseVO;
    }

    @Override
    @Transactional
    public void updateSessionTitle(Long userId, Long sessionId, String title) {
        ChatSession session = sessionMapper.findById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BusinessException(404, "会话不存在");
        }
        sessionMapper.updateTitle(sessionId, title);
    }

    @Override
    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        ChatSession session = sessionMapper.findById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BusinessException(404, "会话不存在");
        }
        // 消息会通过外键级联删除
        sessionMapper.deleteById(sessionId);
    }

    @Override
    @Transactional
    public void clearAllSessions(Long userId) {
        sessionMapper.deleteByUserId(userId);
    }

    /**
     * 根据第一条消息生成会话标题
     */
    private String generateTitle(String firstMessage) {
        if (firstMessage == null || firstMessage.isEmpty()) {
            return "新对话";
        }
        // 截取前20个字符作为标题
        String title = firstMessage.replaceAll("[\\n\\r]", " ").trim();
        if (title.length() > 20) {
            title = title.substring(0, 20) + "...";
        }
        return title;
    }

    /**
     * 转换为会话 VO
     */
    private ChatSessionVO convertToVO(ChatSession session) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setTitle(session.getTitle());
        vo.setCreatedAt(session.getCreatedAt());
        vo.setUpdatedAt(session.getUpdatedAt());
        return vo;
    }

    /**
     * 转换为消息 VO
     */
    private ChatMessageVO convertToMessageVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setRole(message.getRole());
        vo.setContent(message.getContent());
        vo.setCreatedAt(message.getCreatedAt());
        return vo;
    }
}
