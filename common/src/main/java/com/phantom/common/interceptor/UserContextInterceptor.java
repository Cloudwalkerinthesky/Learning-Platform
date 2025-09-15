package com.phantom.common.interceptor;

import com.phantom.common.context.UserContextHolder;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 从请求头中获取用户信息
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");
            String account = request.getHeader("X-Account");
            
            if (userId != null && username != null) {
                UserBaseInfoDTO user = new UserBaseInfoDTO();
                user.setId(Integer.parseInt(userId));
                user.setUsername(username);
                user.setAccount(account);
                
                // 存储到ThreadLocal
                UserContextHolder.setUser(user);
                log.debug("User context set for user: {}", username);
            } else {
                log.warn("Missing user context in request headers");
            }
        } catch (Exception e) {
            log.error("Error setting user context", e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal
        UserContextHolder.clear();
        log.debug("User context cleared");
    }
} 