package com.phantom.learningservice.bean.vo;

import lombok.Data;

@Data
public class UserAssignmentVO {
    private Integer id;
    private Integer userId;
    private Integer assignmentId;
    private String submissionContent;
    private String submissionDate;
    private String grade;
}
