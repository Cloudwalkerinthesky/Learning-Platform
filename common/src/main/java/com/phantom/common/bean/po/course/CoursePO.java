package com.phantom.common.bean.po.course;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程持久化对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 课程ID */
    private Integer id;
    
    /** 课程名称 */
    private String courseName;
    
    /** 教师名称 */
    private String teacherName;
    
    /** 教师ID */
    private Integer teacherId;
    
    /** 课程分类 */
    private String category;
    
    /** 课程描述 */
    private String description;
    
    /** 课程价格 */
    private BigDecimal price;
    
    /** 课程状态：DRAFT-草稿, PUBLISHED-已发布, OFFLINE-已下线 */
    private String status;
    
    /** 课程时长（分钟） */
    private Integer duration;
    
    /** 课程难度：BEGINNER-初级, INTERMEDIATE-中级, ADVANCED-高级 */
    private String difficulty;
    
    /** 课程标签 */
    private String tags;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 课程大纲 */
    private String outline;
    
    /** 学习目标 */
    private String objectives;
    
    /** 先修要求 */
    private String prerequisites;
    
    /** 已报名人数 */
    private Integer enrollmentCount = 0;
    
    /** 课程评分 */
    private BigDecimal rating = BigDecimal.ZERO;
    
    /** 评价数量 */
    private Integer reviewCount = 0;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 版本号（用于乐观锁） */
    private Long version;
    
    /** 是否删除 */
    private Boolean deleted = false;
} 