package com.erickwu.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 配置类
 * 用于配置通义千问等大模型 API
 */
@Configuration
@EnableConfigurationProperties(LlmConfig.class)
@ConfigurationProperties(prefix = "llm")
public class LlmConfig {

    private String provider = "qwen";
    private boolean mockMode = false;
    private QwenConfig qwen = new QwenConfig();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isMockMode() {
        return mockMode;
    }

    public void setMockMode(boolean mockMode) {
        this.mockMode = mockMode;
    }

    public QwenConfig getQwen() {
        return qwen;
    }

    public void setQwen(QwenConfig qwen) {
        this.qwen = qwen;
    }

    /**
     * 通义千问配置
     */
    public static class QwenConfig {
        private String apiKey;
        private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";
        private String model = "qwen-turbo";
        private String embeddingModel = "text-embedding-v2";
        private int timeout = 60000;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getEmbeddingModel() {
            return embeddingModel;
        }

        public void setEmbeddingModel(String embeddingModel) {
            this.embeddingModel = embeddingModel;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }
}
