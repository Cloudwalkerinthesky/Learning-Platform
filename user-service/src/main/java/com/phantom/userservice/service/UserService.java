package com.phantom.userservice.service;

import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.entity.User;

import java.util.Set;

public interface UserService {
    /**
     * 根据ID获取用户收藏信息
     */
    UserFavorVO getUserFavorById(int id);

    /**
     * 根据ID获取用户基本信息
     */
    UserBaseInfoDTO getUserBaseInfoById(int id);
    
    /**
     * 根据用户名获取用户基本信息
     */
    UserBaseInfoDTO getUserBaseInfoByUsername(String username);
    
    /**
     * 更新用户基本信息
     */
    void updateUserBaseInfo(com.phantom.common.bean.dto.UserBaseInfoDTO user);

    /**
     * 根据ID获取用户
     */
    User getById(Long id);

    /**
     * 更新用户信息
     */
    boolean updateById(User user);

    /**
     * 获取用户角色
     */
    Set<String> getUserRoles(Integer userId);

    /**
     * 检查角色是否有指定权限
     */
    boolean hasPermission(String role, String permission);
}
