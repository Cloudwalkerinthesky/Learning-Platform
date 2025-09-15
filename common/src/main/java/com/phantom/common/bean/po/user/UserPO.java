package com.phantom.common.bean.po.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户持久化对象 - 通用版本
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 用户ID */
    private Integer id;
    
    /** 用户名 */
    private String username;
    
    /** 账号 */
    private String account;
    
    /** 密码（加密后） */
    private String password;
    
    /** 邮箱 */
    private String email;
    
    /** 手机号 */
    private String phone;
    
    /** 头像URL */
    private String avatar;
    
    /** 昵称 */
    private String nickname;
    
    /** 性别：MALE-男, FEMALE-女, UNKNOWN-未知 */
    private String gender = "UNKNOWN";
    
    /** 生日 */
    private LocalDateTime birthday;
    
    /** 个人简介 */
    private String bio;
    
    /** 所在地区 */
    private String location;
    
    /** 用户角色：USER-普通用户, TEACHER-教师, ADMIN-管理员 */
    private String role = "USER";
    
    /** 用户状态：ACTIVE-活跃, INACTIVE-非活跃, SUSPENDED-暂停, BANNED-封禁 */
    private String status = "ACTIVE";
    
    /** 注册时间 */
    private LocalDateTime registrationTime;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 最后登录IP */
    private String lastLoginIp;
    
    /** 邮箱验证状态 */
    private Boolean emailVerified = false;
    
    /** 邮箱验证时间 */
    private LocalDateTime emailVerifiedTime;
    
    /** 手机验证状态 */
    private Boolean phoneVerified = false;
    
    /** 手机验证时间 */
    private LocalDateTime phoneVerifiedTime;
    
    /** 密码重置token */
    private String resetToken;
    
    /** 密码重置token过期时间 */
    private LocalDateTime resetTokenExpiry;
    
    /** 登录失败次数 */
    private Integer loginFailCount = 0;
    
    /** 账号锁定时间 */
    private LocalDateTime lockTime;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 版本号（用于乐观锁） */
    private Long version;
    
    /** 是否删除 */
    private Boolean deleted = false;
} 