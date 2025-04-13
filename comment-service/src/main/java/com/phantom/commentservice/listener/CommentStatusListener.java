package com.phantom.commentservice.listener;

import com.phantom.commentservice.mapper.CommentMapper;
import com.phantom.common.CourseStatus;
import com.phantom.common.event.CourseStatusEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentStatusListener {
    @Autowired
    private CommentMapper commentMapper;

    @RabbitListener(queues = "comment.status.queue")
    public void handleCourseStatusUpdate(CourseStatusEvent event){
        System.out.println("收到课程状态更新"+event);
        if (CourseStatus.DELETED.getCode().equals(event.getStatus())){
            commentMapper.deleteCommentByCourseId(event.getCourseId());
            System.out.println("课程 "+event.getCourseName()+" 已被删除，清理评论");
        }
    }
}
