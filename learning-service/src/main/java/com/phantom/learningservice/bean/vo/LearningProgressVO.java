package com.phantom.learningservice.bean.vo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class LearningProgressVO {
    private Integer id;
    private Integer userId;
    private Integer courseId;
    private BigDecimal progress;
    private Timestamp lastUpdated;
}
