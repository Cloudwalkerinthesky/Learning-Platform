package com.phantom.common.bean.po.rbac;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户-角色关联表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRolePO {
    
    /** 主键ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 角色ID */
    private Long roleId;
    
    /** 创建时间 */
    private LocalDateTime createdTime;
    
    public UserRolePO(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
        this.createdTime = LocalDateTime.now();
    }
}
