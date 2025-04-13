package com.phantom.commentservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.phantom.commentservice.bean.dto.CommentDTO;
import com.phantom.commentservice.bean.po.CommentPO;
import com.phantom.commentservice.bean.vo.CommentVO;
import com.phantom.commentservice.feign.UserClient;
import com.phantom.commentservice.mapper.CommentMapper;
import com.phantom.commentservice.service.CommentService;
import com.phantom.commentservice.util.CommentUtil;
import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.common.bean.vo.R;
import com.phantom.common.constant.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserClient userClient;
    @Override
    @Transactional
    public int addComment(CommentDTO commentDTO){
        CommentPO commentPO = CommentUtil.convertDtoToPo(commentDTO);
        //从user-service校验userName
        R<UserBaseInfoDTO> userResponse = userClient.getUserBaseInfoById(commentDTO.getUserId());
        if (userResponse.getCode()== Code.SUCCESS&&userResponse.getData()!=null){
            commentPO.setUserName(userResponse.getData().getUsername());
        }else {
            log.warn("User not found for userId: {},using provided userName:{}",commentDTO.getUserId(),commentDTO.getUserName());
        }

        return commentMapper.addComment(commentPO);
    }
    @Override
    @Transactional
    public void deleteCommentById(int id){
        commentMapper.deleteCommentById(id);
    }

    @Override
    @Transactional
    public CommentVO updateComment(int id,CommentDTO commentDTO){
        CommentPO commentPO=CommentUtil.convertDtoToPo(commentDTO);
        commentPO.setId(id);
        //更新时也同步username
        R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(commentPO.getUserId());
        if(userResponse.getCode()==Code.SUCCESS&&userResponse.getData()!=null){
            commentPO.setUserName(userResponse.getData().getUsername());
        }
        commentMapper.updateComment(commentPO);
        return getCommentById(id);
//        return CommentUtil.convertPoToVo(commentPO);
    }

    @Override
    public CommentVO getCommentById(int id){
        CommentPO commentPO=commentMapper.getCommentById(id);
        if (commentPO==null){
            return null;
        }
        CommentVO commentVO=CommentUtil.convertPoToVo(commentPO);
        //从user-service获取username
        R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(commentPO.getUserId());
        if(userResponse.getCode()==Code.SUCCESS&&userResponse.getData()!=null){
            commentVO.setUserName(userResponse.getData().getUsername());
        }else {
            log.warn("Failed to fetch username for userId: {}, falling back to stored userName:{}",
                    commentPO.getUserId(),commentPO.getUserName());
            commentVO.setUserName(commentPO.getUserName());
        }
        return commentVO;
    }
    @Override
    public List<CommentVO> getAllCommentsOfCourse(int courseId){
    List<CommentPO> commentPOs=commentMapper.getAllCommentsOfCourse(courseId);
    return commentPOs.stream()
            .map(po->{
                CommentVO commentVO=CommentUtil.convertPoToVo(po);
                R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(po.getUserId());
                if(userResponse.getCode()==Code.SUCCESS&&userResponse.getData()!=null){
                    commentVO.setUserName(userResponse.getData().getUsername());
                }else {
                    commentVO.setUserName(po.getUserName());
                }
                return commentVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<CommentVO> getAllCommentsOfOneUserByUserId(int userId){
        List<CommentPO> commentPOs=commentMapper.getAllCommentsOfOneUserByUserId(userId);
        return commentPOs.stream()
                .map(po->{
                    CommentVO commentVO=CommentUtil.convertPoToVo(po);
                    R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(po.getUserId());
                    if(userResponse.getCode()==Code.SUCCESS&&userResponse.getData()!=null){
                        commentVO.setUserName(userResponse.getData().getUsername());
                    }else {
                        commentVO.setUserName(po.getUserName());
                    }
                    return commentVO;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<CommentVO> getCommentsOfCourseInPage(int courseId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<CommentPO> commentPOs=commentMapper.getCommentsOfCourseInPage(courseId);

        return commentPOs.stream()
                .map(po->{
                    CommentVO commentVO=CommentUtil.convertPoToVo(po);
                    R<UserBaseInfoDTO> userResponse=userClient.getUserBaseInfoById(po.getUserId());
                    if(userResponse.getCode()==Code.SUCCESS&&userResponse.getData()!=null){
                        commentVO.setUserName(userResponse.getData().getUsername());
                    }else {
                        commentVO.setUserName(po.getUserName());
                    }
                    return commentVO;
                })
                .collect(Collectors.toList());
    }



}
