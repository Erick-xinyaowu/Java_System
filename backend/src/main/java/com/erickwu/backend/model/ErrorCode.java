package com.erickwu.backend.model;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "数据验证失败"),
    
    // 业务错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    
    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    USER_TOKEN_INVALID(1005, "Token 无效或已过期"),
    USER_TOKEN_EXPIRED(1006, "Token 已过期"),
    
    // 简历相关 2xxx
    RESUME_NOT_FOUND(2001, "简历不存在"),
    RESUME_ALREADY_EXISTS(2002, "简历已存在"),
    
    // 职业测评相关 3xxx
    ASSESSMENT_NOT_FOUND(3001, "测评记录不存在"),
    ASSESSMENT_TYPE_INVALID(3002, "测评类型无效"),
    
    // 职业报告相关 4xxx
    REPORT_NOT_FOUND(4001, "报告不存在"),
    REPORT_GENERATION_FAILED(4002, "报告生成失败"),
    
    // 学习活动相关 5xxx
    STUDY_ACTIVITY_NOT_FOUND(5001, "学习活动不存在");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
