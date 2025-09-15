package com.phantom.userservice.service;

import com.phantom.common.context.UserContextHolder;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class UserContextService {
    private static final String USER_CACHE_KEY_PREFIX = "user:info:";
    private static final String USER_VERSION_KEY_PREFIX = "user:version:";
    private static final String USER_LOCK_KEY_PREFIX = "user:lock:";
    private static final String USER_ROLE_KEY_PREFIX = "user:role:";
    private static final long USER_CACHE_TTL = 30; // 30分钟缓存
    private static final long LOCK_WAIT_TIME = 5; // 获取锁等待时间
    private static final long LOCK_LEASE_TIME = 30; // 锁持有时间
    private static final int BATCH_SIZE = 100; // 批量操作的大小

    // 缓存统计
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong redisHits = new AtomicLong(0);
    private final AtomicLong dbHits = new AtomicLong(0);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RedissonClient redissonClient;

    // 系统启动时预热缓存
    @PostConstruct
    public void warmUpCache() {
        log.info("开始预热用户缓存...");
        try {
            // 这里可以添加预热逻辑，比如加载活跃用户数据
            // 示例：加载最近登录的100个用户
            List<Integer> activeUserIds = getActiveUserIds();
            for (Integer userId : activeUserIds) {
                getUserContext(userId);
            }
            log.info("用户缓存预热完成，共预热{}个用户", activeUserIds.size());
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }

    // 获取活跃用户ID列表（示例方法）
    private List<Integer> getActiveUserIds() {
        // TODO: 实现获取活跃用户ID的逻辑
        return Collections.emptyList();
    }

    // 获取用户上下文信息（优化后的方法）
    public UserBaseInfoDTO getUserContext(Integer userId) {
        if (userId == null) {
            return null;
        }

        totalRequests.incrementAndGet();

        // 1. 首先尝试从ThreadLocal获取
        UserBaseInfoDTO user = UserContextHolder.getUser();
        if (user != null && user.getId().equals(userId)) {
            if (checkVersion(userId, user.getVersion())) {
                cacheHits.incrementAndGet();
                return user;
            }
        }

        // 2. 获取分布式锁
        String lockKey = USER_LOCK_KEY_PREFIX + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 3. 从Redis获取
                    String cacheKey = USER_CACHE_KEY_PREFIX + userId;
                    user = (UserBaseInfoDTO) redisTemplate.opsForValue().get(cacheKey);
                    
                    if (user != null && checkVersion(userId, user.getVersion())) {
                        redisHits.incrementAndGet();
                        UserContextHolder.setUser(user);
                        return user;
                    }

                    // 4. 从数据库获取
                    dbHits.incrementAndGet();
                    com.phantom.userservice.bean.dto.UserBaseInfoDTO serviceUser = userService.getUserBaseInfoById(userId);
                    if (serviceUser != null) {
                        user = new UserBaseInfoDTO();
                        user.setId(serviceUser.getId());
                        user.setUsername(serviceUser.getUsername());
                        user.setAccount(serviceUser.getAccount());
                        user.setVersion(System.currentTimeMillis());
                        user.setRoles(getUserRoles(userId));

                        // 更新Redis缓存
                        redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_TTL, TimeUnit.MINUTES);
                        redisTemplate.opsForValue().set(USER_VERSION_KEY_PREFIX + userId, user.getVersion(), USER_CACHE_TTL, TimeUnit.MINUTES);
                        UserContextHolder.setUser(user);
                    }
                    return user;
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("获取用户{}的锁失败", userId);
                return getFromDatabase(userId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取用户{}的锁被中断", userId);
            return getFromDatabase(userId);
        }
    }

    // 获取用户角色
    private Set<String> getUserRoles(Integer userId) {
        String roleKey = USER_ROLE_KEY_PREFIX + userId;
        Set<String> roles = (Set<String>) redisTemplate.opsForValue().get(roleKey);
        if (roles == null) {
            roles = userService.getUserRoles(userId);
            if (roles != null) {
                redisTemplate.opsForValue().set(roleKey, roles, USER_CACHE_TTL, TimeUnit.MINUTES);
            }
        }
        return roles;
    }

    // 检查用户权限
    public boolean hasPermission(Integer userId, String permission) {
        UserBaseInfoDTO user = getUserContext(userId);
        if (user == null || user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(role -> userService.hasPermission(role, permission));
    }

    // 批量获取用户上下文
    public Map<Integer, UserBaseInfoDTO> batchGetUserContext(List<Integer> userIds) {
        Map<Integer, UserBaseInfoDTO> result = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        // 分批处理
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            List<Integer> batch = userIds.subList(i, Math.min(i + BATCH_SIZE, userIds.size()));
            for (Integer userId : batch) {
                UserBaseInfoDTO user = getUserContext(userId);
                if (user != null) {
                    result.put(userId, user);
                }
            }
        }
        return result;
    }

    // 更新用户上下文
    public void updateUserContext(UserBaseInfoDTO user) {
        if (user == null || user.getId() == null) {
            return;
        }

        String lockKey = USER_LOCK_KEY_PREFIX + user.getId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 1. 更新数据库
                    userService.updateUserBaseInfo(user);
                    
                    // 2. 更新版本号
                    Long newVersion = System.currentTimeMillis();
                    user.setVersion(newVersion);
                    
                    // 3. 更新Redis缓存
                    String cacheKey = USER_CACHE_KEY_PREFIX + user.getId();
                    redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_TTL, TimeUnit.MINUTES);
                    redisTemplate.opsForValue().set(USER_VERSION_KEY_PREFIX + user.getId(), newVersion, USER_CACHE_TTL, TimeUnit.MINUTES);
                    
                    // 4. 更新ThreadLocal
                    UserContextHolder.setUser(user);
                    
                    log.debug("已更新用户{}的上下文信息，新版本号：{}", user.getId(), newVersion);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("更新用户{}的锁获取失败", user.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("更新用户{}的锁被中断", user.getId());
        }
    }

    // 清除用户上下文
    public void clearUserContext(Integer userId) {
        if (userId == null) {
            return;
        }

        String lockKey = USER_LOCK_KEY_PREFIX + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 1. 清除ThreadLocal
                    UserContextHolder.clear();
                    
                    // 2. 清除Redis缓存
                    String cacheKey = USER_CACHE_KEY_PREFIX + userId;
                    redisTemplate.delete(cacheKey);
                    redisTemplate.delete(USER_VERSION_KEY_PREFIX + userId);
                    
                    log.debug("已清除用户{}的上下文信息", userId);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("清除用户{}的锁获取失败", userId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("清除用户{}的锁被中断", userId);
        }
    }

    // 批量清除用户上下文
    public void batchClearUserContext(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        try {
            // 清除ThreadLocal
            UserContextHolder.clear();
            
            // 批量清除Redis缓存
            List<String> keys = new ArrayList<>();
            for (Integer userId : userIds) {
                keys.add(USER_CACHE_KEY_PREFIX + userId);
                keys.add(USER_VERSION_KEY_PREFIX + userId);
            }
            redisTemplate.delete(keys);
            
            log.debug("已批量清除{}个用户的上下文信息", userIds.size());
        } catch (Exception e) {
            log.error("批量清除用户上下文失败", e);
        }
    }

    // 检查版本号
    private boolean checkVersion(Integer userId, Long cacheVersion) {
        if (cacheVersion == null) {
            return false;
        }
        Long redisVersion = (Long) redisTemplate.opsForValue().get(USER_VERSION_KEY_PREFIX + userId);
        return redisVersion != null && redisVersion.equals(cacheVersion);
    }

    // 从数据库获取用户信息（降级处理）
    private UserBaseInfoDTO getFromDatabase(Integer userId) {
        com.phantom.userservice.bean.dto.UserBaseInfoDTO serviceUser = userService.getUserBaseInfoById(userId);
        if (serviceUser != null) {
            UserBaseInfoDTO user = new UserBaseInfoDTO();
            user.setId(serviceUser.getId());
            user.setUsername(serviceUser.getUsername());
            user.setAccount(serviceUser.getAccount());
            user.setVersion(System.currentTimeMillis());
            return user;
        }
        return null;
    }

    // 获取缓存统计信息
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        long total = totalRequests.get();
        stats.put("totalRequests", total);
        stats.put("threadLocalHits", cacheHits.get());
        stats.put("redisHits", redisHits.get());
        stats.put("dbHits", dbHits.get());
        
        if (total > 0) {
            stats.put("threadLocalHitRate", String.format("%.2f%%", (cacheHits.get() * 100.0) / total));
            stats.put("redisHitRate", String.format("%.2f%%", (redisHits.get() * 100.0) / total));
            stats.put("dbHitRate", String.format("%.2f%%", (dbHits.get() * 100.0) / total));
        }
        
        return stats;
    }
}
