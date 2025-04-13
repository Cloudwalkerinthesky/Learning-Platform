package com.phantom.learningservice.bean.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserCourseVO {
    private Integer id;
    private Integer userId;
    private Integer courseId;
    private Timestamp enrollmentDate;
    private String status;
}
