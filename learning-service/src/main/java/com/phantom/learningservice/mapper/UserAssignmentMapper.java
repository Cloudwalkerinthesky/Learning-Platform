package com.phantom.learningservice.mapper;

import com.phantom.learningservice.bean.po.UserAssignmentPO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserAssignmentMapper {
    @Insert("insert into tb_user_assignment(user_id,assignment_id,submission_content,submission_date,score,feedback,status)"+
            "values (#{userId},#{assignmentId},#{submissionContent},#{submissionDate},#{score},#{feedback},#{status})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(UserAssignmentPO userAssignment);

    @Select("select * from tb_user_assignment where user_id=#{userId} and assignment_id=#{assignmentId}")
    UserAssignmentPO findByUserIdAndAssignmentId(@Param("userId") int userId, @Param("assignmentId") int assignmentId);
}
