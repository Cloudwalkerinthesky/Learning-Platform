package com.phantom.commentservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor jwtInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                    HttpServletRequest request=attributes.getRequest();
                    String token=request.getHeader("Authorization");
                    if (token != null&&!token.isEmpty()) {
                        template.header("Authorization",token);
                    }
                }
            }
        };
    }

}
