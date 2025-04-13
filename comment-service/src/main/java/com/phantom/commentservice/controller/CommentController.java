package com.phantom.commentservice.controller;

import com.phantom.commentservice.bean.dto.CommentDTO;
import com.phantom.commentservice.bean.vo.CommentVO;
import com.phantom.commentservice.service.CommentService;
import com.phantom.common.bean.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

   @PostMapping("/post")
    public R postComment(@RequestBody CommentDTO commentDTO){
       try{
           int result=commentService.addComment(commentDTO);
           return R.ok(result);
       }catch (Exception e){
           return R.failed("Failed to add comment: "+e.getMessage());
       }
   }
   @DeleteMapping("/delete/{id}")
    public R deleteComment(@PathVariable Integer id){
       CommentVO comment=commentService.getCommentById(id);
       String userName=comment.getUserName();

       commentService.deleteCommentById(id);

       return R.ok(null,"Deleted comment of "+userName+" successfully");
   }
   @PutMapping("/update/{id}")
    public R<CommentVO> update(@PathVariable Integer id,@RequestBody CommentDTO commentDTO){
       CommentVO commentVO=commentService.updateComment(id,commentDTO);
       return commentVO!=null? R.ok(commentVO):R.failed("Course update failed");
   }
   @GetMapping("/get/id/{id}")
    public R<CommentVO> getCommentById(@PathVariable Integer id){
       CommentVO commentVO=commentService.getCommentById(id);
       if(commentVO!=null){
       return R.ok(commentVO);
       }else {
           return R.failed("comment not found");
       }
   }

   @GetMapping("get/userId/{userId}")
    public R getAllCommentsOfOneUserByUserId(@PathVariable Integer userId){
       List<CommentVO> commentVOs=commentService.getAllCommentsOfOneUserByUserId(userId);
       return R.ok(commentVOs);
   }

   @GetMapping("get/course/{courseId}/pageNum/{pageNum}/pageSize/{pageSize}")
    public R getCommentsOfCourseInPage(@PathVariable Integer courseId,@PathVariable Integer pageNum,@PathVariable Integer pageSize){
       List<CommentVO> commentVOs=commentService.getCommentsOfCourseInPage(courseId,pageNum,pageSize);
       return R.ok(commentVOs);
   }
}
