package com.phantom.apigateway.service;

import com.phantom.apigateway.dto.LoginRequest;
import com.phantom.apigateway.dto.RegisterRequest;
import com.phantom.apigateway.entity.User;
import com.phantom.apigateway.repository.UserRepository;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.common.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    private static final String LOGIN_TOKEN_PREFIX = "login:user:";
    private static final Duration TOKEN_EXPIRE_TIME = Duration.ofHours(24);
    
    /**
     * 用户登录
     */
    public Mono<String> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
            .switchIfEmpty(Mono.error(new RuntimeException("用户名或密码错误")))
            .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
            .switchIfEmpty(Mono.error(new RuntimeException("用户名或密码错误")))
            .flatMap(user -> {
                // 生成JWT token
                UserBaseInfoDTO userInfo = convertToDTO(user);
                String token = jwtUtils.generateToken(userInfo);
                
                // 保存token到Redis
                String redisKey = LOGIN_TOKEN_PREFIX + user.getId();
                return redisTemplate.opsForValue()
                    .set(redisKey, token, TOKEN_EXPIRE_TIME)
                    .thenReturn(token)
                    .doOnSuccess(t -> log.info("用户 {} 登录成功", user.getUsername()))
                    .doOnError(e -> log.error("Redis保存token失败", e));
            })
            .doOnError(e -> log.warn("登录失败: {}", e.getMessage()));
    }
    
    /**
     * 用户注册
     */
    public Mono<Void> register(RegisterRequest request) {
        return userRepository.countByUsername(request.getUsername())
            .filter(count -> count == 0)
            .switchIfEmpty(Mono.error(new RuntimeException("用户名已存在")))
            .then(Mono.fromCallable(() -> {
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setEmail(request.getEmail());
                user.setAccount(request.getAccount());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                return user;
            }))
            .flatMap(userRepository::save)
            .doOnSuccess(user -> log.info("用户 {} 注册成功", user.getUsername()))
            .doOnError(e -> log.error("注册失败", e))
            .then();
    }
    
    /**
     * 用户登出
     */
    public Mono<Void> logout(String token) {
        return Mono.fromCallable(() -> jwtUtils.getUserIdFromToken(token))
            .filter(userId -> userId != null)
            .flatMap(userId -> {
                String redisKey = LOGIN_TOKEN_PREFIX + userId;
                return redisTemplate.delete(redisKey);
            })
            .doOnSuccess(deleted -> {
                if (deleted > 0) {
                    log.info("用户登出成功，token已失效");
                }
            })
            .then();
    }
    
    /**
     * 验证token是否有效
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            // 1. 验证JWT本身
            if (!jwtUtils.validateToken(token)) {
                return false;
            }
            
            // 2. 获取用户ID
            Integer userId = jwtUtils.getUserIdFromToken(token);
            return userId != null;
        })
        .flatMap(isValid -> {
            if (!isValid) {
                return Mono.just(false);
            }
            
            // 3. 验证Redis中的token
            Integer userId = jwtUtils.getUserIdFromToken(token);
            String redisKey = LOGIN_TOKEN_PREFIX + userId;
            return redisTemplate.opsForValue()
                .get(redisKey)
                .map(redisToken -> token.equals(redisToken))
                .defaultIfEmpty(false);
        })
        .onErrorReturn(false);
    }
    
    /**
     * 根据token获取用户信息
     */
    public Mono<UserBaseInfoDTO> getUserFromToken(String token) {
        return Mono.fromCallable(() -> jwtUtils.parseUserFromToken(token))
            .filter(user -> user != null)
            .switchIfEmpty(Mono.error(new RuntimeException("无法解析用户信息")));
    }
    
    /**
     * 转换User实体为DTO
     */
    private UserBaseInfoDTO convertToDTO(User user) {
        UserBaseInfoDTO dto = new UserBaseInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAccount(user.getAccount());
        return dto;
    }
} 