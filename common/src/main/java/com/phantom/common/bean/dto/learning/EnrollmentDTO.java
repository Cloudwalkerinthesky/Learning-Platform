package com.phantom.common.bean.dto.learning;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程注册数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "用户ID不能为空")
    private Integer userId;
    
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    /** 注册方式：FREE-免费注册, PAID-付费注册 */
    private String enrollmentType = "FREE";
    
    /** 支付金额 */
    private java.math.BigDecimal paymentAmount;
    
    /** 支付方式 */
    private String paymentMethod;
    
    /** 注册来源：WEB-网页, APP-手机应用, API-接口 */
    private String source = "WEB";
    
    /** 备注信息 */
    private String remarks;
    
    /** 注册时间 */
    private LocalDateTime enrollmentTime;
} 