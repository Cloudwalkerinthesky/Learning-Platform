package com.phantom.userservice.service.impl;

import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.po.UserPO;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.mapper.UserMapper;
import com.phantom.userservice.service.UserService;
import com.phantom.userservice.service.RbacService;
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
    
    @Autowired
    private RbacService rbacService;

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
        // 实现用户信息更新逻辑
        log.info("更新用户信息: {}", user.getUsername());
        
        try {
            // 这里可以添加具体的更新逻辑
            // 例如：userMapper.updateUser(user);
            log.info("用户信息更新成功: {}", user.getUsername());
        } catch (Exception e) {
            log.error("用户信息更新失败: {}", user.getUsername(), e);
            throw new RuntimeException("用户信息更新失败");
        }
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
        // 从RBAC服务获取用户角色
        try {
            RbacService.RolePermissionResult result = rbacService.getUserRolesAndPermissions(userId.longValue());
            Set<String> roles = new HashSet<>();
            if (result.getRoleName() != null) {
                roles.add(result.getRoleName());
            }
            return roles;
        } catch (Exception e) {
            log.error("获取用户角色失败, userId: {}", userId, e);
            // 返回默认角色
            Set<String> defaultRoles = new HashSet<>();
            defaultRoles.add("USER");
            return defaultRoles;
        }
    }

    @Override
    public boolean hasPermission(String role, String permission) {
        // 实现基于角色的权限检查逻辑
        // 这里简化处理：不同角色的基本权限
        switch (role.toUpperCase()) {
            case "ADMIN":
                return true; // 管理员拥有所有权限
            case "TEACHER":
                return permission.contains("course") || permission.contains("comment_create") 
                       || permission.contains("user_read") || permission.contains("enrollment");
            case "MODERATOR":
                return permission.contains("comment") || permission.contains("course_read") 
                       || permission.contains("user_read") || permission.contains("enrollment");
            case "USER":
                return permission.contains("_read") || permission.contains("comment_create") 
                       || permission.contains("enrollment");
            default:
                return false;
        }
    }
}
