package com.phantom.courseservice.service.impl;

import com.phantom.common.CourseStatus;
import com.phantom.common.bean.vo.R;
import com.phantom.common.event.CourseStatusEvent;
import com.phantom.courseservice.bean.dto.CourseDTO;
import com.phantom.courseservice.bean.dto.UserBaseInfoDTO;
import com.phantom.courseservice.bean.po.CoursePo;
import com.phantom.courseservice.bean.vo.CourseInfoVo;
import com.phantom.courseservice.mapper.CourseMapper;
import com.phantom.courseservice.service.CourseService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;



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
    @Cacheable(value = "courseInfo",key = "#id")
    public CourseInfoVo getCourseInfoById(int id){
        CoursePo coursePo=courseMapper.getCoursePoById(id);
        return convertToVo(coursePo);
    }

    @Override
    @CacheEvict(value = "courseInfo",allEntries = true)
    public CourseInfoVo createCourse(CourseDTO courseDTO){
        CoursePo coursePo=new CoursePo();
        BeanUtils.copyProperties(courseDTO,coursePo);
        courseMapper.insertCourse(coursePo);
        return convertToVo(coursePo);
    }


    @Override
    @CacheEvict(value = "courseInfo",key = "#id")
    public CourseInfoVo updateCourse(int id,CourseDTO courseDTO){
        CoursePo coursePo=new CoursePo();
        BeanUtils.copyProperties(courseDTO,coursePo);
        coursePo.setId(id);
        courseMapper.updateCourse(coursePo);
        return convertToVo(coursePo);
    }


    @Override
    @CacheEvict(value ="courseInfo",key="#id")
    public void deleteCourse(int id){
        courseMapper.deleteCourse(id);
    }

    @Override
    @Cacheable(value = "coursesByCategory",key = "#category")
    public List<CourseInfoVo> getCoursesByCategory(String category){
        List<CoursePo> courses=courseMapper.getCourseByCategory(category);
        return courses.stream().map(this::convertToVo).collect(Collectors.toList());
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
}
