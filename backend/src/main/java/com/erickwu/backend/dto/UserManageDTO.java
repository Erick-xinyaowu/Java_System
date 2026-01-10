package com.erickwu.backend.dto;

import java.time.LocalDate;

/**
 * 用户管理 DTO（创建/更新用户）
 */
public class UserManageDTO {
    private Long id;
    private String username;
    private String password;  // 创建时必填，更新时可选
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private Integer status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
