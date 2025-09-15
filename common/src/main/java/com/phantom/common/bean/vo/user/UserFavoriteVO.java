package com.phantom.common.bean.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户收藏视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoriteVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 收藏ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 课程名称 */
    private String courseName;
    
    /** 课程封面 */
    private String courseCover;
    
    /** 课程分类 */
    private String category;
    
    /** 教师名称 */
    private String teacherName;
    
    /** 课程价格 */
    private BigDecimal price;
    
    /** 课程评分 */
    private BigDecimal rating;
    
    /** 课程状态 */
    private String courseStatus;
    
    /** 收藏时间 */
    private LocalDateTime favoriteTime;
    
    /** 是否已注册 */
    private Boolean isEnrolled = false;
    
    /** 学习进度（如果已注册） */
    private BigDecimal progress;
    
    /** 课程时长（分钟） */
    private Integer duration;
    
    /** 课程难度 */
    private String difficulty;
} 