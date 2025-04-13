package com.phantom.learningservice.mapper;

import com.phantom.learningservice.bean.po.UserCoursePO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserCourseMapper {
    @Insert("insert into tb_user_course(user_id,course_id,enrollment_date,status)"+
    "values (#{userId},#{courseId},#{enrollmentDate},#{status})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(UserCoursePO userCoursePO);

    @Select("select * from tb_user_course where user_id=#{userId} and course_id=#{courseId}")
    UserCoursePO findByUserIdAndCourseId(@Param("userId") int userId, @Param("courseId") int courseId);

    @Delete("delete from tb_user_course where course_id=#{courseId}")
    void deleteByCourseId(@Param("courseId") int courseId);
}


