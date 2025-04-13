package com.phantom.commentservice.bean.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer courseId;
    private String content;
    private Integer rate;
    private Timestamp createdAt;
}
