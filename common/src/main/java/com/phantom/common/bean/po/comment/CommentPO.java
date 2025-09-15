package com.phantom.common.bean.po.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论持久化对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 评论ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 用户名（冗余字段，避免频繁关联查询） */
    private String userName;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 课程名称（冗余字段） */
    private String courseName;
    
    /** 评论内容 */
    private String content;
    
    /** 评分（1-5星） */
    private Integer rate;
    
    /** 父评论ID（用于回复） */
    private Integer parentId;
    
    /** 评论类型：COURSE-课程评论, REPLY-回复评论 */
    private String commentType = "COURSE";
    
    /** 评论状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝 */
    private String status = "PENDING";
    
    /** 点赞数 */
    private Integer likeCount = 0;
    
    /** 回复数量 */
    private Integer replyCount = 0;
    
    /** 是否匿名 */
    private Boolean anonymous = false;
    
    /** 审核人ID */
    private Integer reviewerId;
    
    /** 审核时间 */
    private LocalDateTime reviewedAt;
    
    /** 审核备注 */
    private String reviewNote;
    
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