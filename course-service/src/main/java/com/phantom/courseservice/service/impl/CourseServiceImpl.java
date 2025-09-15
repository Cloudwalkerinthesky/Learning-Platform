package com.phantom.courseservice.service.impl;

import com.phantom.common.CourseStatus;
import com.phantom.common.bean.vo.R;
import com.phantom.common.context.UserContextHolder;
import com.phantom.common.event.CourseStatusEvent;
import com.phantom.courseservice.bean.dto.CourseDTO;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.courseservice.bean.po.CoursePo;
import com.phantom.courseservice.bean.vo.CourseInfoVo;
import com.phantom.courseservice.mapper.CourseMapper;
import com.phantom.courseservice.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String COURSE_CACHE_KEY = "course:info:";
    private static final String COURSE_LIST_CACHE_KEY = "course:list:";
    private static final Random random = new Random();

    @PostConstruct
    public void warmUpCache() {
        log.info("开始预热课程缓存...");
        try {
            // 预热最新课程
            List<CoursePo> latestCourses = courseMapper.getLatestCourse(10);
            for (CoursePo course : latestCourses) {
                String cacheKey = COURSE_CACHE_KEY + course.getId();
                CourseInfoVo courseVo = convertToVo(course);
                redisTemplate.opsForValue().set(cacheKey, courseVo, getRandomExpiration(), TimeUnit.MINUTES);
            }
            log.info("课程缓存预热完成，共预热{}个课程", latestCourses.size());
        } catch (Exception e) {
            log.error("课程缓存预热失败", e);
        }
    }

    @Override
    public CourseInfoVo oldGetCourseInfoById(int courseId){
        CoursePo coursePo=courseMapper.getCoursePoById(courseId);
        if(coursePo==null)
            return null;
        CourseInfoVo courseInfoVo=new CourseInfoVo();
        courseInfoVo.setId(coursePo.getId());
        courseInfoVo.setCourseName(coursePo.getCourseName());
        courseInfoVo.setCategory(coursePo.getCategory());
        courseInfoVo.setTeacherName(coursePo.getTeacherName());
        courseInfoVo.setCreatedAt(coursePo.getCreatedAt());
        courseInfoVo.setUpdatedAt(coursePo.getUpdatedAt());
        courseInfoVo.setPrice(coursePo.getPrice());
        courseInfoVo.setDescription(courseInfoVo.getDescription());
        return courseInfoVo;
    }

    private CourseInfoVo convertToVo(CoursePo coursePo){
        if(coursePo==null){
            return null;
        }
        CourseInfoVo courseInfoVo=new CourseInfoVo();
        BeanUtils.copyProperties(coursePo,courseInfoVo);
        return courseInfoVo;
    }

    @Override
    @Cacheable(value = "courseInfo", key = "#id", unless = "#result == null")
    public CourseInfoVo getCourseInfoById(int id) {
        // 从ThreadLocal获取当前用户信息，无需显式传参
        UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            log.info("用户 {} 正在查看课程 {}", currentUser.getUsername(), id);
            // 可以在这里添加用户访问记录逻辑
        }
        
        incrementCacheStats("courseInfo", false);
        
        CoursePo coursePo = courseMapper.getCoursePoById(id);
        if (coursePo == null) {
            return null;
        }
        
        CourseInfoVo courseVo = convertToVo(coursePo);
        // 异步更新缓存
        updateCacheAsync(id, courseVo);
        return courseVo;
    }

    @Override
    @CacheEvict(value = "courseInfo",allEntries = true)
    public CourseInfoVo createCourse(CourseDTO courseDTO){
        // 从ThreadLocal获取当前用户信息
        UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            log.info("用户 {} 正在创建课程: {}", currentUser.getUsername(), courseDTO.getCourseName());
            // 可以设置课程创建者
            courseDTO.setTeacherName(currentUser.getUsername());
        }
        
        CoursePo coursePo=new CoursePo();
        BeanUtils.copyProperties(courseDTO,coursePo);
        courseMapper.insertCourse(coursePo);
        return convertToVo(coursePo);
    }

    @Override
    @Transactional
    @CacheEvict(value = "courseInfo", key = "#id")
    public CourseInfoVo updateCourse(int id, CourseDTO courseDTO) {
        // 从ThreadLocal获取当前用户信息
        UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            log.info("用户 {} 正在更新课程 {}", currentUser.getUsername(), id);
            
            // 可以在这里添加权限检查逻辑
            // 例如：只有课程创建者或管理员才能更新课程
            CoursePo existingCourse = courseMapper.getCoursePoById(id);
            if (existingCourse != null && !existingCourse.getTeacherName().equals(currentUser.getUsername())) {
                log.warn("用户 {} 尝试更新不属于自己的课程 {}", currentUser.getUsername(), id);
                // 这里可以抛出异常或返回错误
            }
        }
        
        CoursePo coursePo = new CoursePo();
        BeanUtils.copyProperties(courseDTO, coursePo);
        coursePo.setId(id);
        coursePo.setUpdatedAt(new Date());
        
        // 先更新数据库
        courseMapper.updateCourse(coursePo);
        
        // 再更新缓存
        CourseInfoVo courseVo = convertToVo(coursePo);
        String cacheKey = COURSE_CACHE_KEY + id;
        redisTemplate.opsForValue().set(cacheKey, courseVo, getRandomExpiration(), TimeUnit.MINUTES);
        
        return courseVo;
    }

    @Override
    @CacheEvict(value ="courseInfo",key="#id")
    public void deleteCourse(int id){
        // 从ThreadLocal获取当前用户信息
        UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            log.info("用户 {} 正在删除课程 {}", currentUser.getUsername(), id);
            
            // 可以在这里添加权限检查逻辑
            CoursePo existingCourse = courseMapper.getCoursePoById(id);
            if (existingCourse != null && !existingCourse.getTeacherName().equals(currentUser.getUsername())) {
                log.warn("用户 {} 尝试删除不属于自己的课程 {}", currentUser.getUsername(), id);
                // 这里可以抛出异常或返回错误
                throw new RuntimeException("无权限删除此课程");
            }
        }
        
        courseMapper.deleteCourse(id);
    }

    @Override
    @Cacheable(value = "coursesByCategory", key = "#category", unless = "#result.isEmpty()")
    public List<CourseInfoVo> getCoursesByCategory(String category) {
        List<CoursePo> courses = courseMapper.getCourseByCategory(category);
        List<CourseInfoVo> courseVos = courses.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        // 异步更新缓存
        String cacheKey = COURSE_LIST_CACHE_KEY + category;
        redisTemplate.opsForValue().set(cacheKey, courseVos, getRandomExpiration(), TimeUnit.MINUTES);
        
        return courseVos;
    }

    @Override
    @Cacheable(value="latestCourses",key ="#limit")
    public List<CourseInfoVo> getLatestCourses(int limit){
        List<CoursePo> courses=courseMapper.getLatestCourse(limit);
        return courses.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    // 新增方法：更新课程状态并通知 learning-service
    @Override
    @CacheEvict(value = "courseInfo",key="#id")
    public R<String> updateCourseStatus(int id,String status){
            CoursePo coursePo=courseMapper.getCoursePoById(id);
            if(coursePo==null){
                return R.failed("课程不存在");
            }
            coursePo.setStatus(status);
            coursePo.setUpdatedAt(new Date());
            courseMapper.updateCourse(coursePo);
            // 发送 RabbitMQ 事件通知 learning-service 和 comment-service
            CourseStatusEvent event=new CourseStatusEvent();
            event.setCourseId(id);
            event.setStatus(CourseStatus.valueOf(status));
            event.setCourseName(coursePo.getCourseName());
            rabbitTemplate.convertAndSend("course.status.exchange","course.status.update",event);
            return R.ok("课程状态更新成功");
    }

    private void updateCacheAsync(int courseId, CourseInfoVo courseVo) {
        String cacheKey = COURSE_CACHE_KEY + courseId;
        redisTemplate.opsForValue().set(cacheKey, courseVo, getRandomExpiration(), TimeUnit.MINUTES);
    }

    private long getRandomExpiration() {
        // 基础过期时间30分钟，随机增加0-10分钟，防止缓存雪崩
        return 30 + random.nextInt(10);
    }

    private void incrementCacheStats(String cacheKey, boolean isHit) {
        String totalKey = "cache:" + cacheKey + ":total";
        String hitKey = "cache:" + cacheKey + ":hit";
        
        redisTemplate.opsForValue().increment(totalKey, 1);
        if (isHit) {
            redisTemplate.opsForValue().increment(hitKey, 1);
        }
        
        redisTemplate.expire(totalKey, 24, TimeUnit.HOURS);
        redisTemplate.expire(hitKey, 24, TimeUnit.HOURS);
    }
    
    // 添加获取缓存命中率的方法
    public double getCacheHitRatio(String cacheKey) {
        String totalKey = "cache:" + cacheKey + ":total";
        String hitKey = "cache:" + cacheKey + ":hit";
        
        Long total = (Long) redisTemplate.opsForValue().get(totalKey);
        Long hits = (Long) redisTemplate.opsForValue().get(hitKey);
        
        if (total == null || total == 0) {
            return 0.0;
        }
        
        if (hits == null) {
            hits = 0L;
        }
        
        return (double) hits / total * 100;
    }
}
