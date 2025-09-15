package com.phantom.common.bean.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 课程ID */
    private Integer id;
    
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称不能超过100个字符")
    private String courseName;
    
    @NotBlank(message = "教师名称不能为空")
    @Size(max = 50, message = "教师名称不能超过50个字符")
    private String teacherName;
    
    @NotBlank(message = "课程分类不能为空")
    @Size(max = 50, message = "课程分类不能超过50个字符")
    private String category;
    
    @Size(max = 1000, message = "课程描述不能超过1000个字符")
    private String description;
    
    @NotNull(message = "课程价格不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "课程价格不能为负数")
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
} 