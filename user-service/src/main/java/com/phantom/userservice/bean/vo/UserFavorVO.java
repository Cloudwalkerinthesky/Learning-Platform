package com.phantom.userservice.bean.vo;

import com.phantom.userservice.bean.po.UserPO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFavorVO {
    private Long userId;  // 用户ID
//    private Long courseId;  // 课程ID
//    private String courseName;  // 课程名称
//    private LocalDateTime favoriteTime;  // 收藏时间
//    private String category;  // 课程分类
//    private String teacherName;  // 授课老师
    public UserFavorVO(UserPO userPo){
        this.userId = userPo.getId().longValue();  // 假设 UserPO 中有 id 字段
//        this.courseId = userPo.getCourseId();  // 获取课程ID
//        this.courseName = userPo.getCourseName();  // 获取课程名称
//        this.category = userPo.getCategory();  // 获取课程分类
//        this.teacherName = userPo.getTeacherName();  // 获取授课老师
    }
}
