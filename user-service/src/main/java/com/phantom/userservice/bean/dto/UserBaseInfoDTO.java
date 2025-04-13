package com.phantom.userservice.bean.dto;

import com.phantom.userservice.bean.po.UserPO;
import lombok.Data;

@Data
public class UserBaseInfoDTO {
    private Integer id;
    private String username;
    private String account;

    public UserBaseInfoDTO(UserPO userPO){
        this.id=userPO.getId();
        this.username=userPO.getUsername();
        this.account=userPO.getAccount();
    }
}
