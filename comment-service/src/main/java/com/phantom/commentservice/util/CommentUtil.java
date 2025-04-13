package com.phantom.commentservice.util;

import com.phantom.commentservice.bean.dto.CommentDTO;
import com.phantom.commentservice.bean.po.CommentPO;
import com.phantom.commentservice.bean.vo.CommentVO;
import org.springframework.beans.BeanUtils;

public class CommentUtil {

    public static CommentPO convertDtoToPo(CommentDTO commentDTO){
        CommentPO commentPO=new CommentPO();
        BeanUtils.copyProperties(commentDTO,commentPO);
        return commentPO;
    }

    public static CommentVO convertPoToVo(CommentPO commentPO){
        CommentVO commentVO=new CommentVO();
        BeanUtils.copyProperties(commentPO,commentVO,"userName");
        return commentVO;
    }
}
