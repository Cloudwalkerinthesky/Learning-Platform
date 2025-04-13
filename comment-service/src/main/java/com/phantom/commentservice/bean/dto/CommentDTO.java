package com.phantom.commentservice.bean.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentDTO {

    private Integer userId;
    private String userName;
    private Integer courseId;
    private String content;
    private Integer rate;
    private Timestamp createdAt;
}
