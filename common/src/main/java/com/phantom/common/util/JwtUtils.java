package com.phantom.common.util;

import com.phantom.common.bean.dto.UserBaseInfoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    private Key key;
    private static final String LOGIN_TOKEN_PREFIX = "login:user:";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT token
     */
    public String generateToken(UserBaseInfoDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("account", user.getAccount());
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT token（带角色信息）
     */
    public String generateToken(UserBaseInfoDTO user, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("account", user.getAccount());
        claims.put("roles", roles);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token解析用户信息
     */
    public UserBaseInfoDTO parseUserFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            UserBaseInfoDTO user = new UserBaseInfoDTO();
            user.setId(claims.get("userId", Integer.class));
            user.setUsername(claims.get("username", String.class));
            user.setAccount(claims.get("account", String.class));
            
            return user;
        } catch (Exception e) {
            log.error("Error parsing JWT token", e);
            return null;
        }
    }

    /**
     * 从token获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            log.error("Error getting userId from token", e);
            return null;
        }
    }

    /**
     * 从token获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("Error getting username from token", e);
            return null;
        }
    }

    /**
     * 校验token有效性
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 检查token是否过期
            if (claims.getExpiration().before(new Date())) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }

    /**
     * 校验token有效性并与Redis对比（防伪）
     */
    public boolean validateTokenWithRedis(String token) {
        try {
            // 1. 首先校验JWT本身的有效性
            if (!validateToken(token)) {
                return false;
            }

            // 2. 如果Redis可用，检查Redis中的token
            if (redisTemplate != null) {
                Integer userId = getUserIdFromToken(token);
                if (userId != null) {
                    String redisKey = LOGIN_TOKEN_PREFIX + userId;
                    String redisToken = redisTemplate.opsForValue().get(redisKey);
                    return token.equals(redisToken);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error validating token with Redis", e);
            return false;
        }
    }

    /**
     * 从token获取角色信息
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (Set<String>) claims.get("roles");
        } catch (Exception e) {
            log.error("Error getting roles from token", e);
            return null;
        }
    }

    /**
     * 获取token过期时间
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Error getting expiration from token", e);
            return null;
        }
    }

    /**
     * 使token失效（从Redis中删除）
     */
    public void invalidateToken(String token) {
        try {
            if (redisTemplate != null) {
                Integer userId = getUserIdFromToken(token);
                if (userId != null) {
                    String redisKey = LOGIN_TOKEN_PREFIX + userId;
                    redisTemplate.delete(redisKey);
                    log.info("Token invalidated for user: {}", userId);
                }
            }
        } catch (Exception e) {
            log.error("Error invalidating token", e);
        }
    }
} 