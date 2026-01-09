package com.erickwu.backend.service;

import com.erickwu.backend.config.LlmConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 通义千问 LLM 服务
 * 调用通义千问 API 进行文本分析
 */
@Service
public class QwenLlmService {

    private static final Logger logger = LoggerFactory.getLogger(QwenLlmService.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final LlmConfig llmConfig;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public QwenLlmService(LlmConfig llmConfig, ObjectMapper objectMapper) {
        this.llmConfig = llmConfig;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(llmConfig.getQwen().getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(llmConfig.getQwen().getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(llmConfig.getQwen().getTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 调用通义千问进行聊天补全
     *
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @return LLM 响应文本
     */
    public String chat(String systemPrompt, String userMessage) {
        if (llmConfig.isMockMode()) {
            logger.info("LLM Mock 模式，返回模拟响应");
            return getMockResponse(userMessage);
        }

        try {
            String requestBody = buildChatRequest(systemPrompt, userMessage);
            String url = llmConfig.getQwen().getBaseUrl() + "/chat/completions";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + llmConfig.getQwen().getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
                    .build();

            logger.debug("发送请求到通义千问: {}", url);

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    logger.error("通义千问 API 调用失败: {} - {}", response.code(), errorBody);
                    throw new RuntimeException("LLM API 调用失败: " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return extractContent(responseBody);
            }
        } catch (IOException e) {
            logger.error("调用通义千问 API 异常", e);
            throw new RuntimeException("LLM API 调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 构建聊天请求体
     */
    private String buildChatRequest(String systemPrompt, String userMessage) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("model", llmConfig.getQwen().getModel());

            ArrayNode messages = objectMapper.createArrayNode();

            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                ObjectNode systemMsg = objectMapper.createObjectNode();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                messages.add(systemMsg);
            }

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            root.set("messages", messages);
            root.put("temperature", 0.7);
            root.put("max_tokens", 4096);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }

    /**
     * 从响应中提取内容
     */
    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            throw new RuntimeException("无法解析 LLM 响应");
        } catch (Exception e) {
            logger.error("解析 LLM 响应失败: {}", responseBody, e);
            throw new RuntimeException("解析 LLM 响应失败", e);
        }
    }

    /**
     * Mock 模式响应
     */
    private String getMockResponse(String userMessage) {
        return """
                {
                    "candidateName": "测试用户",
                    "contactInfo": {
                        "phone": "13800138000",
                        "email": "test@example.com"
                    },
                    "summary": "这是一份测试简历的摘要",
                    "skills": [
                        {"name": "Java", "level": 4, "category": "编程语言"},
                        {"name": "Spring Boot", "level": 3, "category": "框架"}
                    ],
                    "education": [
                        {"school": "测试大学", "degree": "本科", "major": "计算机科学", "startDate": "2015-09", "endDate": "2019-06"}
                    ],
                    "workExperience": [
                        {"company": "测试公司", "position": "Java开发", "startDate": "2019-07", "endDate": "2023-01", "description": "负责后端开发"}
                    ]
                }
                """;
    }
}
