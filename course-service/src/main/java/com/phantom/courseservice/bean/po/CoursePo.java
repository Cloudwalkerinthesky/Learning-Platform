package com.phantom.courseservice.bean.po;

import lombok.Data;


import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoursePo {
    private Integer id;
    private String courseName;
    private String teacherName;
    private String category;

    private  String description;
    private BigDecimal price;
    private String status;

    private Date createdAt;
    private Date updatedAt;
}
