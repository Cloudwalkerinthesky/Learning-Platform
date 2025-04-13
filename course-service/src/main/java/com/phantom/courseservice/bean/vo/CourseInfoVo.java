package com.phantom.courseservice.bean.vo;

import lombok.Data;

import java.lang.invoke.StringConcatFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CourseInfoVo {
    private Integer id;
    private String courseName;
    private String teacherName;
    private String category;
    private Date createdAt;
    private Date updatedAt;

    private BigDecimal price;
    private String description;

}
