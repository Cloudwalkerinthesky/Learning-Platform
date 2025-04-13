package com.phantom.commentservice.mapper;

import com.phantom.commentservice.bean.po.CommentPO;
import com.phantom.commentservice.bean.vo.CommentVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {

     @Insert("insert into tb_comment(course_id,user_name,user_id,content,rate)"+"" +
             " values (#{courseId},#{userName},#{userId},#{content},#{rate})")
     int addComment(CommentPO commentPO);

     @Delete("delete from tb_comment where id=#{id}")
     void deleteCommentById(@Param("id")Integer id);

     @Delete("delete from tb_comment where course_id=#{courseId}")
     void deleteCommentByCourseId(@Param("courseId")Integer courseId);

     @Update("update tb_comment set content=#{content},rate=#{rate},user_name=#{userName} where id=#{id}")
     int updateComment( CommentPO commentPO);

     @Select("select * from tb_comment where id=#{id}")
     CommentPO getCommentById(@Param("id") Integer id);

     @Select("select * from tb_comment where course_id=#{courseId}")
     List<CommentPO> getAllCommentsOfCourse(@Param("courseId")Integer courseId);

     @Select("select * from tb_comment where user_id=#{userId}")
     List<CommentPO> getAllCommentsOfOneUserByUserId(@Param("userId") Integer userId);

     @Select("select * from tb_comment where course_id=#{courseId} order by created_at desc ")
     List<CommentPO> getCommentsOfCourseInPage(@Param("courseId") Integer courseId);


}
