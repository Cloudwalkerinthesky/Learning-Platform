package com.phantom.commentservice.service;

import com.phantom.commentservice.bean.dto.CommentDTO;
import com.phantom.commentservice.bean.vo.CommentVO;

import java.util.List;

public interface CommentService {
    int addComment(CommentDTO commentDTO);
    void deleteCommentById(int id);
    CommentVO updateComment(int id,CommentDTO commentDTO);
    CommentVO getCommentById(int id);

    List<CommentVO> getAllCommentsOfCourse(int courseId);
    List<CommentVO> getAllCommentsOfOneUserByUserId(int userId);
    List<CommentVO> getCommentsOfCourseInPage(int courseId,int pageNum,int pageSize);
}
