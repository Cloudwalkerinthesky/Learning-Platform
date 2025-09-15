package com.phantom.common.bean.vo.course;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 课程ID */
    private Integer id;
    
    /** 课程名称 */
    private String courseName;
    
    /** 教师名称 */
    private String teacherName;
    
    /** 课程分类 */
    private String category;
    
    /** 课程描述 */
    private String description;
    
    /** 课程价格 */
    private BigDecimal price;
    
    /** 课程状态 */
    private String status;
    
    /** 课程时长（分钟） */
    private Integer duration;
    
    /** 课程难度 */
    private String difficulty;
    
    /** 课程标签 */
    private String tags;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 学习目标 */
    private String objectives;
    
    /** 已报名人数 */
    private Integer enrollmentCount;
    
    /** 课程评分 */
    private BigDecimal rating;
    
    /** 评价数量 */
    private Integer reviewCount;
    
    /** 是否收藏 */
    private Boolean isFavorited;
    
    /** 学习进度（百分比） */
    private Integer progress;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 