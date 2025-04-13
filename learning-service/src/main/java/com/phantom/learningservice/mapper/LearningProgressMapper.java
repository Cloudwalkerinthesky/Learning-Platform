package com.phantom.learningservice.mapper;

import com.phantom.learningservice.bean.po.LearningProgressPO;
import com.phantom.learningservice.bean.vo.LearningProgressVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.*;

@Mapper

public interface LearningProgressMapper {
    @Insert("insert into tb_learning_progress(user_id,course_id,progress,last_updated)"+
    "values (#{userId},#{courseId},#{progress},#{lastUpdated})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(LearningProgressPO learningProgress);

    @Update("UPDATE tb_learning_progress " +
            "SET progress=#{progress}, last_updated=#{lastUpdated} " +
            "WHERE id=#{id}")
    void update(LearningProgressPO learningProgress);

    @Select("select * from tb_learning_progress where user_id=#{userId} and course_id=#{courseId}")
    LearningProgressPO findByUserIdAndCourseId(@Param("userId") int userId,@Param("courseId") int courseId);
}
