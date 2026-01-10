package com.erickwu.backend.mapper;

import com.erickwu.backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM users ORDER BY created_at DESC")
    List<User> findAll();

    /**
     * 搜索用户（按用户名、邮箱、昵称、手机号）
     */
    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%') " +
            "OR nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR phone LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC")
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * 插入用户
     */
@Insert("INSERT INTO users (username, password, email, phone, nickname, avatar, gender, birthday, status) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{nickname}, #{avatar}, #{gender}, #{birthday}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE users SET nickname = #{nickname}, phone = #{phone}, avatar = #{avatar}, " +
            "gender = #{gender}, birthday = #{birthday}, updated_at = NOW() WHERE id = #{id}")
    int update(User user);

    /**
     * 更新密码
     */
    @Update("UPDATE users SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 禁用/启用用户
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);
}
