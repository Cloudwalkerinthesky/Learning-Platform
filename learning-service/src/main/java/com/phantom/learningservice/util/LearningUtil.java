package com.phantom.learningservice.util;

import com.phantom.learningservice.bean.po.LearningProgressPO;
import com.phantom.learningservice.bean.po.UserAssignmentPO;
import com.phantom.learningservice.bean.po.UserCoursePO;
import com.phantom.learningservice.bean.po.UserCoursePO;
import com.phantom.learningservice.bean.vo.LearningProgressVO;
import com.phantom.learningservice.bean.vo.UserAssignmentVO;
import com.phantom.learningservice.bean.vo.UserCourseVO;
import org.springframework.beans.BeanUtils;

public class LearningUtil {
    public static UserCourseVO convertToVO(UserCoursePO po){
        UserCourseVO vo=new UserCourseVO();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }
    public static LearningProgressVO convertToVO(LearningProgressPO po){
        LearningProgressVO vo=new LearningProgressVO();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }
    public static UserAssignmentVO convertToVO(UserAssignmentPO po){
        UserAssignmentVO vo=new UserAssignmentVO();
        BeanUtils.copyProperties(po,vo);
        return vo;
    }


}
