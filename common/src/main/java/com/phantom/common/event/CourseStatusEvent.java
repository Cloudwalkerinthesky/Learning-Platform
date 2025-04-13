package com.phantom.common.event;

import com.phantom.common.CourseStatus;
import lombok.Data;

@Data
public class CourseStatusEvent {
    private Integer courseId;
    private String status;
    private String courseName;
    public void setStatus(CourseStatus status){
        this.status=status.getCode();
    }
}
