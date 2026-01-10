package com.erickwu.backend.service.impl;

import com.erickwu.backend.config.BusinessException;
import com.erickwu.backend.config.JwtConfig;
import com.erickwu.backend.dto.LoginDTO;
import com.erickwu.backend.dto.RegisterDTO;
import com.erickwu.backend.dto.UpdateUserDTO;
import com.erickwu.backend.entity.User;
import com.erickwu.backend.mapper.UserMapper;
import com.erickwu.backend.model.ErrorCode;
import com.erickwu.backend.service.UserService;
import com.erickwu.backend.util.JwtUtil;
import com.erickwu.backend.vo.LoginVO;
import com.erickwu.backend.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, JwtConfig jwtConfig) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public LoginVO register(RegisterDTO dto) {
        // 检查用户名是否已存在
        if (existsByUsername(dto.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (dto.getEmail() != null && existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "邮箱已被注册");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1); // 正常状态

        // 保存用户
        userMapper.insert(user);

        // 生成 Token 并返回
        return createLoginVO(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 根据用户名查询用户
        User user = userMapper.findByUsername(dto.getUsername());
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR, "密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED, "账号已被禁用");
        }

        // 生成 Token 并返回
        return createLoginVO(user);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        return convertToUserVO(user);
    }

    @Override
    public void updateUser(Long userId, UpdateUserDTO dto) {
        User user = userMapper.findById(userId);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 更新用户信息
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            User existingUser = userMapper.findByEmail(dto.getEmail());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "邮箱已被其他用户使用");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getSchool() != null) {
            user.setSchool(dto.getSchool());
        }
        if (dto.getMajor() != null) {
            user.setMajor(dto.getMajor());
        }
        if (dto.getIntro() != null) {
            user.setIntro(dto.getIntro());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }

        userMapper.update(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.findByEmail(email) != null;
    }

    /**
     * 创建登录响应 VO
     */
    private LoginVO createLoginVO(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        // 过期时间转换为秒
        Long expiresIn = jwtConfig.getExpiration() / 1000;

        return new LoginVO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                token,
                expiresIn
        );
    }

    /**
     * 将 User 实体转换为 UserVO
     */
    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSchool(user.getSchool());
        vo.setMajor(user.getMajor());
        vo.setIntro(user.getIntro());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
