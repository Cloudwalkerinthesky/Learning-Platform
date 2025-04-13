package com.phantom.learningservice.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


@Data
public class AssignmentPO {

    private Integer id;


    private Integer courseId;
    private String title;
    private String description;

    private Timestamp dueDate;


    private BigDecimal maxScore;
    private BigDecimal weight;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
