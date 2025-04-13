package com.phantom.courseservice.messaging;

import com.phantom.courseservice.bean.po.CoursePo;
import com.phantom.courseservice.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseMessageConsumer {
    private final CourseMapper courseMapper;

    @RabbitListener(queues = "course.enrollment.queue")
    public void handleEnrollmentUpdate(Map<String,Object> message){
        Integer courseId=(Integer) message.get("courseId");
        Integer userId=(Integer) message.get("userId");
        Long timestamp=(Long) message.get("timestamp");
        log.info("Received enrollment update:userId={},courseId={},timestamp={}",userId,courseId,timestamp);

        CoursePo coursePo=courseMapper.getCoursePoById(courseId);
        if(coursePo==null){
            log.warn("Course not found for courseId:{}",courseId);
            return;
        }
        log.info("Course {} (name:{}) has a new enrollment by user {}",courseId,coursePo.getCourseName(),userId);
        //扩展：如更新状态到CLOSED
    }
}
