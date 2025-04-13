package com.phantom.courseservice.controller;

import com.phantom.common.bean.vo.R;
import com.phantom.courseservice.bean.dto.CourseDTO;
import com.phantom.courseservice.bean.vo.CourseInfoVo;
import com.phantom.courseservice.service.CourseService;
import com.phantom.courseservice.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;
    @Autowired
    private MinioService minioService;

    @GetMapping("/test/{id}")
    public String test(@PathVariable Integer id){
        System.out.println(id);
        return id.toString();
    }

    @GetMapping("/info/old/{id}")
    public R oldGetCourseInfoById(@PathVariable Integer id){
        CourseInfoVo courseInfoVo= courseService.oldGetCourseInfoById(id);
        if(courseInfoVo==null){
            return R.failed("doesn't exist");
        }
        else
            return R.ok(courseInfoVo);
    }

    @GetMapping("/info/{id}")
    public R getCourseInfoById(@PathVariable Integer id){
        CourseInfoVo courseInfoVo= courseService.getCourseInfoById(id);
        return courseInfoVo!=null?R.ok(courseInfoVo):R.failed("Course doesn't exist");
    }

    @PostMapping("/create")
    public R createCourse(@RequestBody CourseDTO courseDTO) {
        logger.info("Received request to create course: {}", courseDTO);
        try {
            CourseInfoVo courseInfoVo = courseService.createCourse(courseDTO);
            logger.info("Course created successfully: {}", courseInfoVo);
            return R.ok(courseInfoVo);
        } catch (Exception e) {
            logger.error("Error creating course", e);
            return R.failed("Failed to create course: " + e.getMessage());
        }
    }


    @PutMapping("/update/{id}")
    public R<CourseInfoVo> updateCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        CourseInfoVo courseInfoVo = courseService.updateCourse(id, courseDTO);
        return courseInfoVo != null ? R.ok(courseInfoVo) : R.failed("Course update failed");
    }

    @DeleteMapping("/delete/{id}")
    public R<Void> deleteCourse(@PathVariable Integer id){
        courseService.deleteCourse(id);
        return R.ok();
    }
    @GetMapping("/category/{category}")
    public R<List<CourseInfoVo>> getCoursesByCategory(@PathVariable String category){
        List<CourseInfoVo> courses=courseService.getCoursesByCategory(category);
        return R.ok(courses);
    }
    @GetMapping("/latest/{limit}")
    public R<List<CourseInfoVo>>getLatestCourses(@PathVariable Integer limit){
        List<CourseInfoVo> courses=courseService.getLatestCourses(limit);
        return R.ok(courses);
    }

    @PostMapping("/status/{id}")
    public R<String> updateCourseStatus(@PathVariable("id") int id, @RequestParam("status")String status){
        return courseService.updateCourseStatus(id, status);
    }

    //关于视频和文件的操作
    @PostMapping("/upload/material")
    public R<String> uploadCourseMaterial(@RequestParam("file") MultipartFile file) {
        try {
            String url = minioService.uploadFile(file, "course-materials");
            return R.ok("文件上传成功，URL: " + url);
        } catch (Exception e) {
            return R.failed("上传失败： " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity downloadCourseMaterial(@PathVariable String fileName){
        try {
            InputStream inputStream=minioService.downloadFile("course-materials",fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        }catch (Exception e){
            return ResponseEntity.status(500).body("下载失败: " + e.getMessage());
        }
    }


}

