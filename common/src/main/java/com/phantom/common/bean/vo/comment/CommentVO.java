package com.phantom.common.bean.vo.comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 评论ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 用户名 */
    private String userName;
    
    /** 用户头像 */
    private String userAvatar;
    
    /** 课程ID */
    private Integer courseId;
    
    /** 课程名称 */
    private String courseName;
    
    /** 评论内容 */
    private String content;
    
    /** 评分 */
    private Integer rate;
    
    /** 父评论ID */
    private Integer parentId;
    
    /** 父评论用户名 */
    private String parentUserName;
    
    /** 评论类型 */
    private String commentType;
    
    /** 评论状态 */
    private String status;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 当前用户是否点赞 */
    private Boolean isLiked = false;
    
    /** 是否匿名 */
    private Boolean anonymous;
    
    /** 子评论列表 */
    private List<CommentVO> replies;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 格式化的创建时间 */
    private String formattedCreatedAt;
} 