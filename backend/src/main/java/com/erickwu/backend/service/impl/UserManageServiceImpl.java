package com.erickwu.backend.service.impl;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.dto.UserManageDTO;
import com.erickwu.backend.entity.User;
import com.erickwu.backend.mapper.UserMapper;
import com.erickwu.backend.service.UserManageService;
import com.erickwu.backend.vo.UserManageVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserManageServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserManageVO> getAllUsers() {
        List<User> users = userMapper.findAll();
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserManageVO> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        List<User> users = userMapper.searchUsers(keyword.trim());
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserManageVO getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserManageVO createUser(UserManageDTO dto) {
        // 验证用户名唯一性
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 验证邮箱唯一性
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (userMapper.countByEmail(dto.getEmail()) > 0) {
                throw new BusinessException(400, "邮箱已被使用");
            }
        }
        
        // 验证密码
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender() != null ? dto.getGender() : 0);
        user.setBirthday(dto.getBirthday());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserManageVO updateUser(UserManageDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        User existingUser = userMapper.findById(dto.getId());
        if (existingUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 如果修改了邮箱，检查唯一性
        if (dto.getEmail() != null && !dto.getEmail().equals(existingUser.getEmail())) {
            if (userMapper.countByEmail(dto.getEmail()) > 0) {
                throw new BusinessException(400, "邮箱已被使用");
            }
        }

        // 更新用户信息
        existingUser.setNickname(dto.getNickname());
        existingUser.setPhone(dto.getPhone());
        existingUser.setAvatar(dto.getAvatar());
        existingUser.setGender(dto.getGender());
        existingUser.setBirthday(dto.getBirthday());

        userMapper.update(existingUser);

        // 如果提供了新密码，更新密码
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            userMapper.updatePassword(dto.getId(), passwordEncoder.encode(dto.getPassword()));
        }

        // 如果修改了状态
        if (dto.getStatus() != null && !dto.getStatus().equals(existingUser.getStatus())) {
            userMapper.updateStatus(dto.getId(), dto.getStatus());
        }

        return getUserById(dto.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 不允许删除 admin 用户
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(403, "不能删除管理员账户");
        }

        userMapper.delete(id);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 不允许禁用 admin 用户
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new BusinessException(403, "不能禁用管理员账户");
        }

        userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException(400, "新密码不能为空");
        }

        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    /**
     * 转换为 VO（不包含密码）
     */
    private UserManageVO convertToVO(User user) {
        UserManageVO vo = new UserManageVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());
        return vo;
    }
}
