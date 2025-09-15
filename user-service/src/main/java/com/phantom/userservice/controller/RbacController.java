package com.phantom.userservice.controller;

import com.phantom.common.bean.vo.R;
import com.phantom.userservice.service.RbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * RBAC权限管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/rbac")
public class RbacController {
    
    @Autowired
    private RbacService rbacService;
    
    /**
     * 获取用户的角色和权限信息
     */
    @GetMapping("/roles-permissions/{userId}")
    public R<Map<String, Object>> getUserRolesAndPermissions(@PathVariable Long userId) {
        try {
            RbacService.RolePermissionResult result = rbacService.getUserRolesAndPermissions(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("role", result.getRoleName());
            data.put("permissions", result.getPermissions());
            
            return R.ok(data);
        } catch (Exception e) {
            log.error("获取用户角色权限失败, userId: {}", userId, e);
            return R.failed("获取用户角色权限失败");
        }
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/assign-role")
    public R<String> assignRoleToUser(@RequestParam Long userId, @RequestParam String roleName) {
        try {
            boolean success = rbacService.assignRoleToUser(userId, roleName);
            if (success) {
                return R.ok("角色分配成功");
            } else {
                return R.failed("角色分配失败");
            }
        } catch (Exception e) {
            log.error("分配角色失败, userId: {}, roleName: {}", userId, roleName, e);
            return R.failed("分配角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否有指定权限
     */
    @GetMapping("/check-permission/{userId}")
    public R<Boolean> checkPermission(@PathVariable Long userId, @RequestParam String permission) {
        try {
            boolean hasPermission = rbacService.hasPermission(userId, permission);
            return R.ok(hasPermission);
        } catch (Exception e) {
            log.error("检查权限失败, userId: {}, permission: {}", userId, permission, e);
            return R.failed("检查权限失败");
        }
    }
    
    /**
     * 检查用户是否有指定角色
     */
    @GetMapping("/check-role/{userId}")
    public R<Boolean> checkRole(@PathVariable Long userId, @RequestParam String roleName) {
        try {
            boolean hasRole = rbacService.hasRole(userId, roleName);
            return R.ok(hasRole);
        } catch (Exception e) {
            log.error("检查角色失败, userId: {}, roleName: {}", userId, roleName, e);
            return R.failed("检查角色失败");
        }
    }
    
    /**
     * 初始化RBAC基础数据（管理员接口）
     */
    @PostMapping("/init-data")
    public R<String> initRbacData() {
        try {
            rbacService.initializeRbacData();
            return R.ok("RBAC基础数据初始化成功");
        } catch (Exception e) {
            log.error("初始化RBAC数据失败", e);
            return R.failed("初始化失败: " + e.getMessage());
        }
    }
}
