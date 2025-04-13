package com.phantom.learningservice.service;

import com.phantom.learningservice.bean.dto.AssignmentSubmissionDTO;
import com.phantom.learningservice.bean.dto.EnrollmentDTO;
import com.phantom.learningservice.bean.dto.ProgressUpdateDTO;
import com.phantom.learningservice.bean.vo.AssignmentVO;
import com.phantom.learningservice.bean.vo.LearningProgressVO;
import com.phantom.learningservice.bean.vo.UserAssignmentVO;
import com.phantom.learningservice.bean.vo.UserCourseVO;

public interface LearningService {
    UserCourseVO enrollUserInCourse(EnrollmentDTO enrollmentDTO);
    LearningProgressVO updateLearningProgress(ProgressUpdateDTO progressUpdateDTO);
    UserAssignmentVO submitAssignment(AssignmentSubmissionDTO submissionDTO);
}
