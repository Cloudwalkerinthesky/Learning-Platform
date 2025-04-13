package com.phantom.courseservice.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseDTO {
    private String courseName;
    private String teacherName;
    private String category;
    private String description;
    private BigDecimal price;
    private String status;

}
