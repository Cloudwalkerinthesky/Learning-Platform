package com.phantom.common.bean.po.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习进度持久化对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressPO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 进度记录ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 章节ID */
    private Integer chapterId;
    
    /** 课时ID */
    private Integer lessonId;
    
    /** 学习进度（百分比） */
    private BigDecimal progress = BigDecimal.ZERO;
    
    /** 本次学习时长（分钟） */
    private Integer studyDuration = 0;
    
    /** 累计学习时长（分钟） */
    private Integer totalStudyTime = 0;
    
    /** 学习状态：STUDYING-学习中, PAUSED-暂停, COMPLETED-已完成 */
    private String studyStatus = "STUDYING";
    
    /** 开始学习时间 */
    private LocalDateTime startTime;
    
    /** 最后更新时间 */
    private LocalDateTime lastUpdated;
    
    /** 完成时间 */
    private LocalDateTime completedTime;
    
    /** 学习设备：PC-电脑, MOBILE-手机, TABLET-平板 */
    private String device = "PC";
    
    /** 学习位置标记（视频时间点等） */
    private String position;
    
    /** IP地址 */
    private String ipAddress;
    
    /** 用户代理信息 */
    private String userAgent;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 版本号（用于乐观锁） */
    private Long version;
    
    /** 是否删除 */
    private Boolean deleted = false;
} 