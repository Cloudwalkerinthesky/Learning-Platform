package com.phantom.common.bean.po.rbac;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色-权限关联表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionPO {
    
    /** 主键ID */
    private Long id;
    
    /** 角色ID */
    private Long roleId;
    
    /** 权限ID */
    private Long permissionId;
    
    /** 创建时间 */
    private LocalDateTime createdTime;
    
    public RolePermissionPO(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createdTime = LocalDateTime.now();
    }
}
