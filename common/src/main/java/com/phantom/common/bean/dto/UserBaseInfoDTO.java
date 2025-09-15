package com.phantom.common.bean.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 * 用户基本信息DTO - 通用版本
 * 用于服务间传递用户基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBaseInfoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 用户ID */
    private Integer id;
    
    /** 用户名 */
    private String username;
    
    /** 账号 */
    private String account;
    
    /** 邮箱 */
    private String email;
    
    /** 版本号（用于乐观锁） */
    private Long version;
    
    /** 用户角色集合 */
    private Set<String> roles;
    
    /**
     * 基础构造函数
     */
    public UserBaseInfoDTO(Integer id, String username, String account) {
        this.id = id;
        this.username = username;
        this.account = account;
        this.roles = new HashSet<>();
    }
    
    /**
     * 带邮箱的构造函数
     */
    public UserBaseInfoDTO(Integer id, String username, String account, String email) {
        this(id, username, account);
        this.email = email;
    }
    
    /**
     * 添加角色
     */
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }
    
    /**
     * 移除角色
     */
    public void removeRole(String role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }
    
    /**
     * 判断是否有指定角色
     */
    public boolean hasRole(String role) {
        return this.roles != null && this.roles.contains(role);
    }
    
    /**
     * 获取角色数量
     */
    public int getRoleCount() {
        return this.roles != null ? this.roles.size() : 0;
    }
}