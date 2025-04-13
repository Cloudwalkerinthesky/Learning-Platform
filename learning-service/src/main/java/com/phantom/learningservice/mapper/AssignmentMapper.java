package com.phantom.learningservice.mapper;

import com.phantom.learningservice.bean.po.AssignmentPO;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AssignmentMapper {
    @Insert("INSERT INTO tb_assignment (course_id, title, description, due_date, max_score, weight, created_at, updated_at) " +
            "VALUES (#{courseId}, #{title}, #{description}, #{dueDate}, #{maxScore}, #{weight}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AssignmentPO assignment);

    @Select("SELECT * from tb_assignment where id=#{id}")
    AssignmentPO findById(Long id);

}
