package com.phantom.learningservice.bean.dto;

import lombok.Data;

@Data
public class AssignmentSubmissionDTO {
    private Integer userId;
    private Integer assignmentId;
    private Integer submission;
}
