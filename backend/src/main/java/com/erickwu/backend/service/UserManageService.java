package com.erickwu.backend.service;

import com.erickwu.backend.dto.UserManageDTO;
import com.erickwu.backend.vo.UserManageVO;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface UserManageService {
    
    /**
     * 获取所有用户列表
     */
    List<UserManageVO> getAllUsers();
    
    /**
     * 根据关键词搜索用户
     */
    List<UserManageVO> searchUsers(String keyword);
    
    /**
     * 根据ID获取用户
     */
    UserManageVO getUserById(Long id);
    
    /**
     * 创建用户
     */
    UserManageVO createUser(UserManageDTO dto);
    
    /**
     * 更新用户
     */
    UserManageVO updateUser(UserManageDTO dto);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long id, Integer status);
    
    /**
     * 重置用户密码
     */
    void resetPassword(Long id, String newPassword);
}
