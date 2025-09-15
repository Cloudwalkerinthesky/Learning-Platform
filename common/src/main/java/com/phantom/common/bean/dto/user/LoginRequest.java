package com.phantom.common.bean.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户登录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度应该在3-20之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应该在6-20之间")
    private String password;
    
    /** 记住我 */
    private boolean rememberMe = false;
    
    /** 验证码 */
    private String captcha;
    
    /** 验证码key */
    private String captchaKey;
} 