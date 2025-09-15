package com.phantom.common.bean.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 评论ID */
    private Integer id;
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    /** 用户名 */
    private String userName;
    
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    /** 课程名称 */
    private String courseName;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer rate;
    
    /** 父评论ID（用于回复） */
    private Integer parentId;
    
    /** 评论类型：COURSE-课程评论, REPLY-回复评论 */
    private String commentType = "COURSE";
    
    /** 评论状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝 */
    private String status = "PENDING";
    
    /** 点赞数 */
    private Integer likeCount = 0;
    
    /** 是否匿名 */
    private Boolean anonymous = false;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 