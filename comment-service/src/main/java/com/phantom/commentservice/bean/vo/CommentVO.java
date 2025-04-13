package com.phantom.commentservice.bean.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentVO {

    private String userName;
    private String content;
    private Integer rate;
    private Timestamp createdAt;
}
