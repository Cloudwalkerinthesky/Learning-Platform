package com.phantom.learningservice.feign;


import com.phantom.common.bean.vo.CourseInfoVo;
import com.phantom.common.bean.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseClient {
    @GetMapping("/course/info/{id}")
    R<CourseInfoVo> getCourseInfo(@PathVariable("id") Integer id);
}
