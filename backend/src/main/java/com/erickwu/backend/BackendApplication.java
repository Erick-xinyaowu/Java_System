package com.erickwu.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Career Planner 后端应用程序入口
 * 智能职业规划与学业分析系统
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Career Planner Backend Started!");
        System.out.println("  Server running on: http://localhost:8080");
        System.out.println("===========================================");
    }
}
