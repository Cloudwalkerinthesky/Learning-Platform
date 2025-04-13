package com.phantom.userservice.bean.po;

import lombok.Data;

@Data
public class UserPO {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String account;
    private String role;


}
