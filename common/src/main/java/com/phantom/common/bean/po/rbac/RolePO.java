package com.phantom.common.bean.po.rbac;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePO {
    
    /** 角色ID */
    private Long id;
    
    /** 角色名称 (user, admin, teacher, moderator) */
    private String name;
    
    /** 角色描述 */
    private String description;
    
    /** 创建时间 */
    private LocalDateTime createdTime;
    
    /** 更新时间 */
    private LocalDateTime updatedTime;
    
    public RolePO(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
