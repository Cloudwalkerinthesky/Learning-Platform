package com.phantom.apigateway.controller;

import com.phantom.apigateway.dto.ApiResponse;
import com.phantom.apigateway.dto.LoginRequest;
import com.phantom.apigateway.dto.RegisterRequest;
import com.phantom.apigateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<String>>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
            .map(token -> ResponseEntity.ok(ApiResponse.success("登录成功", token)))
            .onErrorResume(e -> {
                log.error("登录失败", e);
                return Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.failed(e.getMessage())));
            });
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<Void>>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
            .then(Mono.just(ResponseEntity.ok(ApiResponse.success("注册成功", null))))
            .onErrorResume(e -> {
                log.error("注册失败", e);
                return Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.failed(e.getMessage())));
            });
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<ApiResponse<Void>>> logout(ServerHttpRequest request) {
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            return Mono.just(ResponseEntity.badRequest()
                .body(ApiResponse.failed("未找到token")));
        }
        
        return authService.logout(token)
            .then(Mono.just(ResponseEntity.ok(ApiResponse.success("登出成功", null))))
            .onErrorResume(e -> {
                log.error("登出失败", e);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failed("登出失败: " + e.getMessage())));
            });
    }
    
    /**
     * 验证token有效性（内部接口，可用于健康检查）
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> validateToken(ServerHttpRequest request) {
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            return Mono.just(ResponseEntity.ok(ApiResponse.success(false)));
        }
        
        return authService.validateToken(token)
            .map(isValid -> ResponseEntity.ok(ApiResponse.success(isValid)))
            .onErrorReturn(ResponseEntity.ok(ApiResponse.success(false)));
    }
    
    /**
     * 从请求中提取token
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
} 