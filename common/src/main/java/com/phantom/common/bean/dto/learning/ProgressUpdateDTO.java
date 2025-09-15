package com.phantom.common.bean.dto.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习进度更新数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    /** 章节ID */
    private Integer chapterId;
    
    /** 课时ID */
    private Integer lessonId;
    
    @NotNull(message = "学习进度不能为空")
    @DecimalMin(value = "0.00", message = "学习进度不能小于0")
    @DecimalMax(value = "100.00", message = "学习进度不能大于100")
    private BigDecimal progress;
    
    /** 本次学习时长（分钟） */
    private Integer studyDuration;
    
    /** 学习状态：STUDYING-学习中, PAUSED-暂停, COMPLETED-已完成 */
    private String studyStatus = "STUDYING";
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 学习设备：PC-电脑, MOBILE-手机, TABLET-平板 */
    private String device = "PC";
    
    /** 学习位置标记（视频时间点等） */
    private String position;
} 