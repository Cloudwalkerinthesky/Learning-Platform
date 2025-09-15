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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final WebClient.Builder webClientBuilder;
    
    private static final String LOGIN_TOKEN_PREFIX = "login:user:";
    private static final Duration TOKEN_EXPIRE_TIME = Duration.ofHours(24);
    
    /**
     * 用户登录 - 集成RBAC权限
     */
    public Mono<String> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
            .switchIfEmpty(Mono.error(new RuntimeException("用户名或密码错误")))
            .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
            .switchIfEmpty(Mono.error(new RuntimeException("用户名或密码错误")))
            .flatMap(user -> {
                // 获取用户角色权限信息 - 修复类型转换
                return getUserRolesAndPermissions(user.getId().longValue())
                    .flatMap(rolePermissions -> {
                        // 生成包含角色权限的JWT token
                        UserBaseInfoDTO userInfo = convertToDTO(user);
                        
                        // 添加角色信息到用户信息中
                        if (rolePermissions.containsKey("role")) {
                            userInfo.addRole(rolePermissions.get("role").toString());
                        }
                        
                        String token = jwtUtils.generateTokenWithRolePermissions(
                            userInfo, 
                            rolePermissions.get("role") != null ? rolePermissions.get("role").toString() : "USER",
                            getPermissionsList(rolePermissions.get("permissions"))
                        );
                        
                        // 保存token到Redis
                        String redisKey = LOGIN_TOKEN_PREFIX + user.getId();
                        return redisTemplate.opsForValue()
                            .set(redisKey, token, TOKEN_EXPIRE_TIME)
                            .thenReturn(token)
                            .doOnSuccess(t -> log.info("用户 {} 登录成功，角色: {}", 
                                user.getUsername(), rolePermissions.get("role")))
                            .doOnError(e -> log.error("Redis保存token失败", e));
                    })
                    .onErrorResume(e -> {
                        log.warn("获取用户角色权限失败，使用默认权限: {}", e.getMessage());
                        // 降级处理：使用默认权限
                        UserBaseInfoDTO userInfo = convertToDTO(user);
                        userInfo.addRole("USER");
                        String token = jwtUtils.generateTokenWithRolePermissions(userInfo, "USER", List.of());
                        
                        String redisKey = LOGIN_TOKEN_PREFIX + user.getId();
                        return redisTemplate.opsForValue()
                            .set(redisKey, token, TOKEN_EXPIRE_TIME)
                            .thenReturn(token);
                    });
            })
            .doOnError(e -> log.warn("登录失败: {}", e.getMessage()));
    }
    
    /**
     * 调用user-service获取用户角色权限
     */
    private Mono<Map<String, Object>> getUserRolesAndPermissions(Long userId) {
        return webClientBuilder.build()
            .get()
            .uri("http://user-service/user/rbac/roles-permissions/{userId}", userId)
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                if (response.get("code").equals(200)) {
                    Object data = response.get("data");
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> result = (Map<String, Object>) data;
                        return result;
                    } else {
                        throw new RuntimeException("获取角色权限失败: 数据格式错误");
                    }
                } else {
                    throw new RuntimeException("获取角色权限失败: " + response.get("message"));
                }
            })
            .doOnError(e -> log.error("调用user-service获取角色权限失败", e));
    }
    
    /**
     * 用户注册 - 集成RBAC
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
            .flatMap(user -> {
                // 为新用户分配默认角色
                return assignDefaultRoleToUser(user.getId().longValue())
                    .doOnSuccess(success -> {
                        if (success) {
                            log.info("用户 {} 注册成功，已分配默认角色", user.getUsername());
                        } else {
                            log.warn("用户 {} 注册成功，但分配默认角色失败", user.getUsername());
                        }
                    })
                    .onErrorResume(e -> {
                        log.warn("用户 {} 注册成功，但分配默认角色失败: {}", user.getUsername(), e.getMessage());
                        return Mono.just(false); // 降级处理，不影响注册流程
                    })
                    .then();
            })
            .doOnError(e -> log.error("注册失败", e));
    }
    
    /**
     * 为用户分配默认角色
     */
    private Mono<Boolean> assignDefaultRoleToUser(Long userId) {
        return webClientBuilder.build()
            .post()
            .uri("http://user-service/user/rbac/assign-role?userId={userId}&roleName={roleName}", 
                 userId, "USER")
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                if (response.get("code").equals(200)) {
                    return true;
                } else {
                    log.error("分配默认角色失败: {}", response.get("message"));
                    return false;
                }
            })
            .onErrorReturn(false);
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
    
    /**
     * 安全地转换权限列表
     */
    private List<String> getPermissionsList(Object permissions) {
        if (permissions instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> result = (List<String>) permissions;
            return result;
        }
        return List.of(); // 返回空列表作为默认值
    }
} 