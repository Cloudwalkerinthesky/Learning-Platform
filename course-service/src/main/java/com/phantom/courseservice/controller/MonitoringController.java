package com.phantom.courseservice.controller;

import com.phantom.common.bean.vo.R;
import com.phantom.courseservice.service.impl.CourseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {

    private final CourseServiceImpl courseService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/cache/hit-ratio/{cacheName}")
    public R<Double> getCacheHitRatio(@PathVariable String cacheName) {
        try {
            double hitRatio = courseService.getCacheHitRatio(cacheName);
            return R.ok(hitRatio);
        } catch (Exception e) {
            log.error("获取缓存命中率失败", e);
            return R.failed("获取缓存命中率失败: " + e.getMessage());
        }
    }

    @GetMapping("/cache/stats")
    public R<Map<String, Object>> getAllCacheStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取所有缓存类型的命中率
            stats.put("courseInfo", courseService.getCacheHitRatio("courseInfo"));
            stats.put("coursesByCategory", courseService.getCacheHitRatio("coursesByCategory"));
            stats.put("latestCourses", courseService.getCacheHitRatio("latestCourses"));
            
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取缓存统计数据失败", e);
            return R.failed("获取缓存统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/redis/info")
    public R<Map<String, Object>> getRedisInfo() {
        try {
            // 获取Redis服务器信息
            String info = redisTemplate.getConnectionFactory().getConnection().info().toString();
            
            // 解析关键信息
            Map<String, Object> redisInfo = new HashMap<>();
            
            // 添加缓存命中率相关信息
            redisInfo.put("keyspace_hits", getRedisInfoValue(info, "keyspace_hits"));
            redisInfo.put("keyspace_misses", getRedisInfoValue(info, "keyspace_misses"));
            
            // 计算Redis层面的缓存命中率
            long hits = Long.parseLong(getRedisInfoValue(info, "keyspace_hits"));
            long misses = Long.parseLong(getRedisInfoValue(info, "keyspace_misses"));
            double hitRatio = hits + misses > 0 ? (double) hits / (hits + misses) * 100 : 0;
            redisInfo.put("hit_ratio", String.format("%.2f%%", hitRatio));
            
            return R.ok(redisInfo);
        } catch (Exception e) {
            log.error("获取Redis信息失败", e);
            return R.failed("获取Redis信息失败: " + e.getMessage());
        }
    }
    
    private String getRedisInfoValue(String info, String key) {
        if (info.contains(key)) {
            int start = info.indexOf(key) + key.length() + 1;
            int end = info.indexOf("\r\n", start);
            if (end == -1) {
                end = info.length();
            }
            return info.substring(start, end);
        }
        return "0";
    }
} 