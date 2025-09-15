package com.phantom.common.bean.vo.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户课程关系视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 关系ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 用户名 */
    private String username;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 课程名称 */
    private String courseName;
    
    /** 课程封面 */
    private String courseCover;
    
    /** 注册时间 */
    private LocalDateTime enrollmentDate;
    
    /** 状态：ACTIVE-学习中, COMPLETED-已完成, SUSPENDED-暂停, EXPIRED-已过期 */
    private String status;
    
    /** 学习进度（百分比） */
    private BigDecimal progress;
    
    /** 最后学习时间 */
    private LocalDateTime lastStudyTime;
    
    /** 总学习时长（分钟） */
    private Integer totalStudyTime;
    
    /** 证书获得状态 */
    private Boolean certificateEarned = false;
    
    /** 证书获得时间 */
    private LocalDateTime certificateEarnedTime;
    
    /** 课程评分（用户给出的） */
    private BigDecimal userRating;
    
    /** 是否收藏 */
    private Boolean isFavorited = false;
} 