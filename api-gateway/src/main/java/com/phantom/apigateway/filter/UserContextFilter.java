package com.phantom.apigateway.filter;

import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从JWT中获取用户信息
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 解析JWT获取用户信息
                UserBaseInfoDTO user = jwtUtils.parseUserFromToken(token);
                if (user != null) {
                    // 将用户信息添加到请求头
                    exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(user.getId()))
                            .header("X-Username", user.getUsername())
                            .header("X-Account", user.getAccount())
                            .build();
                    log.debug("User context added to request headers for user: {}", user.getUsername());
                }
            } catch (Exception e) {
                log.error("Error parsing user from token", e);
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }
} 