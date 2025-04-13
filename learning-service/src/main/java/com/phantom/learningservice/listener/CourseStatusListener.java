package com.phantom.learningservice.listener;

import com.phantom.common.CourseStatus;
import com.phantom.common.event.CourseStatusEvent;
import com.phantom.learningservice.mapper.UserCourseMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class CourseStatusListener {
    @Autowired
    private UserCourseMapper userCourseMapper;

    @RabbitListener(queues = "course.status.queue")
    public void handleCourseStatusUpdate(CourseStatusEvent event){
        System.out.println("收到课程状态更新"+event);
        if(CourseStatus.DELETED.getCode().equals(event.getStatus())){
            userCourseMapper.deleteByCourseId(event.getCourseId());
        }else if(CourseStatus.PUBLISHED.getCode().equals(event.getStatus())){
            System.out.println("课程"+event.getCourseName()+"已发布,可选课");
        }
    }
}
