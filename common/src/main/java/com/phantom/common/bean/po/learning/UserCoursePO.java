package com.phantom.common.bean.po.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户课程关系持久化对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoursePO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 关系ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 注册时间 */
    private LocalDateTime enrollmentDate;
    
    /** 状态：ACTIVE-学习中, COMPLETED-已完成, SUSPENDED-暂停, EXPIRED-已过期 */
    private String status;
    
    /** 学习进度（百分比） */
    private BigDecimal progress = BigDecimal.ZERO;
    
    /** 最后学习时间 */
    private LocalDateTime lastStudyTime;
    
    /** 总学习时长（分钟） */
    private Integer totalStudyTime = 0;
    
    /** 注册方式：FREE-免费注册, PAID-付费注册 */
    private String enrollmentType = "FREE";
    
    /** 支付金额 */
    private BigDecimal paymentAmount;
    
    /** 支付时间 */
    private LocalDateTime paymentTime;
    
    /** 证书获得状态 */
    private Boolean certificateEarned = false;
    
    /** 证书获得时间 */
    private LocalDateTime certificateEarnedTime;
    
    /** 课程评分（用户给出的） */
    private BigDecimal userRating;
    
    /** 是否收藏 */
    private Boolean isFavorited = false;
    
    /** 过期时间（付费课程） */
    private LocalDateTime expirationDate;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 版本号（用于乐观锁） */
    private Long version;
    
    /** 是否删除 */
    private Boolean deleted = false;
} 