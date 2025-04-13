package com.phantom.courseservice.mapper;

import com.phantom.courseservice.bean.po.CoursePo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CourseMapper {
    @Select("SELECT id, courseName, teacherName, category, createdAt, updatedAt FROM tb_course WHERE id = #{id}")
    CoursePo oldGetCoursePoById(@Param("id")Integer id);

    @Select("SELECT id, courseName, teacherName, category, createdAt, updatedAt FROM tb_course WHERE id = #{id}")
    CoursePo getCoursePoById(@Param("id")Integer id);

    @Insert("INSERT INTO tb_course (courseName, teacherName, category, description, price, status, createdAt, updatedAt) " +
            "VALUES (#{courseName}, #{teacherName}, #{category}, #{description}, #{price}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertCourse(CoursePo course);

    @Update("update tb_course set courseName=#{courseName},teacherName=#{teacherName},"+
    "category=#{category},description=#{description},price=#{price},"+
    "status=#{status},updatedAt=Now() where id=#{id}")
    int updateCourse (CoursePo course);

    @Delete("delete from tb_course where id=#{id}")
    int deleteCourse(@Param("id") Integer id);

    @Select("SELECT * from tb_course where category=#{category}")
    List<CoursePo> getCourseByCategory(@Param("category") String category);

    @Select("SELECT * from tb_course order by createdAt desc limit #{limit}")
    List<CoursePo> getLatestCourse(@Param("limit") int limit);

}

