package com.erickwu.backend.service;

import com.erickwu.backend.dto.LoginDTO;
import com.erickwu.backend.dto.RegisterDTO;
import com.erickwu.backend.dto.UpdateUserDTO;
import com.erickwu.backend.vo.LoginVO;
import com.erickwu.backend.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册请求参数
     * @return 登录响应（包含token）
     */
    LoginVO register(RegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录请求参数
     * @return 登录响应（包含token）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息VO
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新请求参数
     */
    void updateUser(Long userId, UpdateUserDTO dto);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true 如果存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true 如果存在
     */
    boolean existsByEmail(String email);
}
