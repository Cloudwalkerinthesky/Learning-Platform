package com.phantom.learningservice.controller;

import com.phantom.learningservice.bean.dto.EnrollmentDTO;
import com.phantom.learningservice.bean.vo.UserCourseVO;
import com.phantom.learningservice.service.LearningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learning")
@Slf4j
public class LearningController {
    @Autowired
    private LearningService learningService;

    @PostMapping("/enroll")
    public ResponseEntity<UserCourseVO> enrollUserInCourse(@RequestBody EnrollmentDTO enrollmentDTO){
        UserCourseVO userCourseVO=learningService.enrollUserInCourse(enrollmentDTO);
        return ResponseEntity.ok(userCourseVO);
    }
}
