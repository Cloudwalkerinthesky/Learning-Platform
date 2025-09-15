package com.phantom.userservice.service;

import com.phantom.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStateCacheService {
    private static final String USER_STATE_KEY_PREFIX = "user:state:";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）
    private static final long LOCK_WAIT_TIME = 5; // 获取锁等待时间（秒）
    private static final long LOCK_LEASE_TIME = 10; // 锁租约时间（秒）

    private final RedisTemplate<String, User> redisTemplate;
    private final RedissonClient redissonClient;
    private final UserService userService;

    /**
     * 获取用户状态（三级缓存）
     */
    public User getUserState(Long userId) {
        // 1. 尝试从Redis获取
        String cacheKey = USER_STATE_KEY_PREFIX + userId;
        User user = redisTemplate.opsForValue().get(cacheKey);
        
        if (user != null) {
            log.debug("Cache hit for user: {}", userId);
            return user;
        }

        // 2. Redis未命中，尝试获取分布式锁
        RLock lock = redissonClient.getLock("lock:" + cacheKey);
        try {
            // 尝试获取锁，最多等待LOCK_WAIT_TIME秒
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 双重检查，防止其他线程已经更新了缓存
                    user = redisTemplate.opsForValue().get(cacheKey);
                    if (user != null) {
                        return user;
                    }

                    // 3. 从数据库加载
                    user = userService.getById(userId);
                    if (user != null) {
                        // 更新Redis缓存
                        redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
                    }
                    return user;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to acquire lock for user: {}", userId, e);
            Thread.currentThread().interrupt();
        }

        // 4. 如果获取锁失败，直接查询数据库
        return userService.getById(userId);
    }

    /**
     * 更新用户状态
     */
    public void updateUserState(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        String cacheKey = USER_STATE_KEY_PREFIX + user.getId();
        RLock lock = redissonClient.getLock("lock:" + cacheKey);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 更新数据库
                    userService.updateById(user);
                    // 更新Redis缓存
                    redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("Failed to update user state: {}", user.getId(), e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 删除用户状态缓存
     */
    public void removeUserState(Long userId) {
        if (userId == null) {
            return;
        }

        String cacheKey = USER_STATE_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }
} 