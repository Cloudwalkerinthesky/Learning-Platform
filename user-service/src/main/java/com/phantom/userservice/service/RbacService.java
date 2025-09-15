package com.phantom.userservice.service;

import com.phantom.common.bean.po.rbac.PermissionPO;
import com.phantom.common.bean.po.rbac.RolePO;

import java.util.List;
import java.util.Set;

/**
 * RBAC权限管理服务接口
 */
public interface RbacService {
    
    /**
     * 获取用户的角色和权限
     * 返回格式：{roleName, [permission1, permission2, ...]}
     */
    RolePermissionResult getUserRolesAndPermissions(Long userId);
    
    /**
     * 为用户分配默认角色(USER)
     */
    boolean assignDefaultRole(Long userId);
    
    /**
     * 为用户分配指定角色
     */
    boolean assignRoleToUser(Long userId, String roleName);
    
    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String permissionName);
    
    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(Long userId, String roleName);
    
    /**
     * 初始化RBAC基础数据
     */
    void initializeRbacData();
    
    /**
     * 角色权限查询结果封装类
     */
    class RolePermissionResult {
        private String roleName;
        private List<String> permissions;
        
        public RolePermissionResult(String roleName, List<String> permissions) {
            this.roleName = roleName;
            this.permissions = permissions;
        }
        
        public String getRoleName() {
            return roleName;
        }
        
        public List<String> getPermissions() {
            return permissions;
        }
        
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
        
        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}
