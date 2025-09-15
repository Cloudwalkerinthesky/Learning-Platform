package com.phantom.common.bean.po.rbac;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionPO {
    
    /** 权限ID */
    private Long id;
    
    /** 权限名称 (delete_comment, delete_course, manage_users) */
    private String name;
    
    /** 权限操作 (create, read, update, delete) */
    private String action;
    
    /** 权限资源 (course, comment, user) */
    private String resource;
    
    /** 权限描述 */
    private String description;
    
    /** 创建时间 */
    private LocalDateTime createdTime;
    
    /** 更新时间 */
    private LocalDateTime updatedTime;
    
    public PermissionPO(String name, String action, String resource, String description) {
        this.name = name;
        this.action = action;
        this.resource = resource;
        this.description = description;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
