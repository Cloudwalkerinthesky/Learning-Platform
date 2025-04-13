package com.phantom.learningservice.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
public class UserCoursePO {

    private Integer id;

    private Integer userId;

    private Integer courseId;

    private Timestamp enrollmentDate;
    private String status;
}
