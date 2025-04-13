package com.phantom.userservice.service.impl;

import com.phantom.userservice.UserServiceApplication;
import com.phantom.userservice.bean.dto.LoginDTO;
import com.phantom.userservice.bean.dto.RegisterDTO;
import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.po.UserPO;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.mapper.UserMapper;
import com.phantom.userservice.security.JwtUtils;
import com.phantom.userservice.service.UserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;


    @Override
    public UserFavorVO getUserFavorById(int id){
        UserPO userPo=userMapper.getUserPoById(id);
        if (userPo == null) {
            return null;
        }
        return new UserFavorVO(userPo);
    }

    @Override
    public UserBaseInfoDTO getUserBaseInfoById(int id){
        UserPO userPO=userMapper.getUserPoById(id);
        if(userPO==null){
            return null;
        }
        return new UserBaseInfoDTO(userPO);
    }
    @Override
    @Transactional
    public void register(RegisterDTO registerDTO){
        if(userMapper.existsByUsername(registerDTO.getUsername())){
            throw new RuntimeException("Username exists");
        }
        String encodedPassword=passwordEncoder.encode(registerDTO.getPassword());

        UserPO user=new UserPO();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(registerDTO.getEmail());
        user.setAccount(registerDTO.getAccount());
        userMapper.insert(user);
    }

    @Override
    public String login(LoginDTO loginDTO) {  // 新增登录方法
        UserPO user = userMapper.getUserByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");  // 用户名或密码错误时抛出 BadCredentialsException
        }
        return jwtUtils.generateToken(user.getUsername(),user.getId());

//        UserDetails userDetails = new User(user.getUsername(), user.getPassword(),
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));  // 为用户设置角色权限
//        return jwtUtils.generateToken(userDetails.getUsername());  // 使用 JwtUtils 生成 JWT
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public UserBaseInfoDTO getUserBaseInfoByUsername(String username){
        UserPO userPO=userMapper.getUserByUsername(username);
        if(userPO==null){
            return null;
        }
        return new UserBaseInfoDTO(userPO);
    }
}
