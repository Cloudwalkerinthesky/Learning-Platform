package com.phantom.apigateway.filter;

import com.phantom.apigateway.service.AuthService;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final AuthService authService;

    // 白名单路径，不需要token校验
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register", 
            "/auth/validate",
            "/user/test",
            "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.debug("Processing request: {}", path);

        // 检查是否在白名单中
        if (isWhiteListed(path)) {
            log.debug("Path {} is whitelisted, skipping authentication", path);
            return chain.filter(exchange);
        }

        // 提取token
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("No token found in request for path: {}", path);
            return handleUnauthorized(exchange, "缺少认证token");
        }

        // 使用AuthService验证token
        return authService.validateToken(token)
            .flatMap(isValid -> {
                if (!isValid) {
                    log.warn("Invalid token for path: {}", path);
                    return handleUnauthorized(exchange, "token无效或已过期");
                }
                
                // 获取用户信息
                return authService.getUserFromToken(token);
            })
            .flatMap(user -> {
                // 将用户信息添加到请求头，透传给下游服务
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(user.getId()))
                        .header("X-Username", user.getUsername())
                        .header("X-Account", user.getAccount())
                        .header("Authorization", "Bearer " + token) // 保持原始token透传
                        .build();

                log.debug("Authentication successful for user: {} on path: {}", user.getUsername(), path);

                // 继续执行过滤链
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            })
            .onErrorResume(e -> {
                log.error("Error during authentication for path: {}", path, e);
                return handleUnauthorized(exchange, "认证过程发生错误");
            });
    }

    /**
     * 提取token
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(whitePath -> 
            path.startsWith(whitePath) || path.matches(whitePath.replace("**", ".*"))
        );
    }

    /**
     * 处理未授权请求
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }
}
