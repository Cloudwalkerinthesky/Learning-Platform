package com.phantom.learningservice.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


@Data
public class UserAssignmentPO {

    private Integer id;
    private Integer userId;
    private Integer assignmentId;
    private String submissionContent;
    private Timestamp submissionDate;
    private BigDecimal score;
    private String feedback;
    private String status;

}
