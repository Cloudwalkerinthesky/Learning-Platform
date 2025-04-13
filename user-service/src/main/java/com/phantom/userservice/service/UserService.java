package com.phantom.userservice.service;

import com.phantom.userservice.bean.dto.LoginDTO;
import com.phantom.userservice.bean.dto.RegisterDTO;
import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.vo.UserFavorVO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserFavorVO getUserFavorById(int id);

    UserBaseInfoDTO getUserBaseInfoById(int id);
    void register(RegisterDTO registerDTO);

    String login(LoginDTO loginDTO);
    UserDetails  loadUserByUsername(String username);

    UserBaseInfoDTO getUserBaseInfoByUsername(String username);
}
