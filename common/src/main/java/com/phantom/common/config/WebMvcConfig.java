package com.phantom.common.config;

import com.phantom.common.interceptor.UserContextInterceptor;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserContextInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register");
    }

    /**
     * Feign请求拦截器，自动传递token
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    // 从当前请求上下文获取token
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest request = attributes.getRequest();
                        
                        // 获取Authorization header
                        String authorization = request.getHeader("Authorization");
                        if (authorization != null && !authorization.isEmpty()) {
                            template.header("Authorization", authorization);
                            log.debug("Token added to Feign request: {}", template.url());
                        }
                        
                        // 传递用户信息headers
                        String userId = request.getHeader("X-User-Id");
                        String username = request.getHeader("X-Username");
                        String account = request.getHeader("X-Account");
                        String roles = request.getHeader("X-User-Roles");
                        
                        if (userId != null) {
                            template.header("X-User-Id", userId);
                        }
                        if (username != null) {
                            template.header("X-Username", username);
                        }
                        if (account != null) {
                            template.header("X-Account", account);
                        }
                        if (roles != null) {
                            template.header("X-User-Roles", roles);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error adding token to Feign request", e);
                }
            }
        };
    }
} 