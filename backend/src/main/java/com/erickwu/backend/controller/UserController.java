package com.erickwu.backend.controller;

import com.erickwu.backend.dto.LoginDTO;
import com.erickwu.backend.dto.RegisterDTO;
import com.erickwu.backend.dto.UpdateUserDTO;
import com.erickwu.backend.model.ApiResponse;
import com.erickwu.backend.service.UserService;
import com.erickwu.backend.util.UserContext;
import com.erickwu.backend.vo.LoginVO;
import com.erickwu.backend.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户注册、登录、信息获取和更新等操作
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param dto 注册请求参数
     * @return 登录响应（包含token）
     */
    @PostMapping("/register")
    public ApiResponse<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        LoginVO loginVO = userService.register(dto);
        return ApiResponse.success("注册成功", loginVO);
    }

    /**
     * 用户登录
     *
     * @param dto 登录请求参数
     * @return 登录响应（包含token）
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return ApiResponse.success("登录成功", loginVO);
    }

    /**
     * 获取当前用户信息
     * 需要登录（携带Token）
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public ApiResponse<UserVO> getUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return ApiResponse.success(userVO);
    }

    /**
     * 更新当前用户信息
     * 需要登录（携带Token）
     *
     * @param dto 更新请求参数
     * @return 成功响应
     */
    @PutMapping("/update")
    public ApiResponse<Void> updateUser(@Valid @RequestBody UpdateUserDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateUser(userId, dto);
        return ApiResponse.success("更新成功", null);
    }

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return true 如果可用（不存在）
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.success(!exists);
    }

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return true 如果可用（不存在）
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(!exists);
    }
}
