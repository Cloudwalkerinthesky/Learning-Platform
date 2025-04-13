package com.phantom.learningservice.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgressUpdateDTO {
    private Integer userId;
    private Integer courseId;
    private BigDecimal progress;
}
