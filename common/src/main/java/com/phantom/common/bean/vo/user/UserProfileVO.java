package com.phantom.common.bean.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户资料视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 用户ID */
    private Integer id;
    
    /** 用户名 */
    private String username;
    
    /** 账号 */
    private String account;
    
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
    
    /** 用户状态：ACTIVE-活跃, INACTIVE-非活跃, SUSPENDED-暂停, BANNED-封禁 */
    private String status;
    
    /** 用户角色 */
    private Set<String> roles;
    
    /** 注册时间 */
    private LocalDateTime registrationTime;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 邮箱验证状态 */
    private Boolean emailVerified = false;
    
    /** 手机验证状态 */
    private Boolean phoneVerified = false;
    
    /** 学习统计 */
    private UserLearningStats learningStats;
    
    /**
     * 用户学习统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLearningStats implements Serializable {
        /** 已注册课程数 */
        private Integer enrolledCourses = 0;
        
        /** 已完成课程数 */
        private Integer completedCourses = 0;
        
        /** 总学习时长（分钟） */
        private Integer totalStudyTime = 0;
        
        /** 获得证书数 */
        private Integer certificatesEarned = 0;
        
        /** 收藏课程数 */
        private Integer favoriteCourses = 0;
    }
} 