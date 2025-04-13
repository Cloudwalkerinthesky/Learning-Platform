package com.phantom.learningservice.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


@Data
public class LearningProgressPO {

    private Integer id;


    private Integer userId;

    private Integer courseId;
    private BigDecimal progress;

    private Timestamp lastUpdated;
}
