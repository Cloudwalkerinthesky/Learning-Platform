package com.phantom.courseservice.service;

import com.phantom.common.bean.vo.R;
import com.phantom.courseservice.CourseServiceApplication;
import com.phantom.courseservice.bean.dto.CourseDTO;
import com.phantom.courseservice.bean.vo.CourseInfoVo;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface CourseService {
  CourseInfoVo oldGetCourseInfoById(int courseId);

  CourseInfoVo getCourseInfoById(int id);


  CourseInfoVo createCourse(CourseDTO courseDTO);
  CourseInfoVo updateCourse(int id,CourseDTO courseDTO);
  void deleteCourse(int id);
  List<CourseInfoVo> getCoursesByCategory(String category);
  List<CourseInfoVo> getLatestCourses(int limit);
  R<String> updateCourseStatus(int id,String status);
}
