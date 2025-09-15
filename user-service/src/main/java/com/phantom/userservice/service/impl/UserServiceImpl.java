package com.phantom.userservice.service.impl;

import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.po.UserPO;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.mapper.UserMapper;
import com.phantom.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserFavorVO getUserFavorById(int id){
        UserPO userPo = userMapper.getUserPoById(id);
        if (userPo == null) {
            return null;
        }
        return new UserFavorVO(userPo);
    }

    @Override
    public UserBaseInfoDTO getUserBaseInfoById(int id){
        UserPO userPO = userMapper.getUserPoById(id);
        if(userPO == null){
            return null;
        }
        return new UserBaseInfoDTO(userPO);
    }

    @Override
    public UserBaseInfoDTO getUserBaseInfoByUsername(String username){
        UserPO userPO = userMapper.getUserByUsername(username);
        if(userPO == null){
            return null;
        }
        return new UserBaseInfoDTO(userPO);
    }

    @Override
    public void updateUserBaseInfo(com.phantom.common.bean.dto.UserBaseInfoDTO user) {
        // TODO: 实现用户信息更新逻辑
        log.info("更新用户信息: {}", user.getUsername());
    }

    @Override
    public com.phantom.userservice.entity.User getById(Long id) {
        // TODO: 实现根据ID获取用户逻辑
        return null;
    }

    @Override
    public boolean updateById(com.phantom.userservice.entity.User user) {
        // TODO: 实现更新用户逻辑
        return false;
    }

    @Override
    public Set<String> getUserRoles(Integer userId) {
        // TODO: 从数据库获取用户角色，这里先返回默认角色
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        return roles;
    }

    @Override
    public boolean hasPermission(String role, String permission) {
        // TODO: 实现权限检查逻辑
        return true;
    }
}
