package com.phantom.userservice.service.impl;

import com.phantom.common.bean.po.rbac.PermissionPO;
import com.phantom.common.bean.po.rbac.RolePO;
import com.phantom.common.bean.po.rbac.RolePermissionPO;
import com.phantom.common.bean.po.rbac.UserRolePO;
import com.phantom.userservice.mapper.PermissionMapper;
import com.phantom.userservice.mapper.RoleMapper;
import com.phantom.userservice.mapper.RolePermissionMapper;
import com.phantom.userservice.mapper.UserRoleMapper;
import com.phantom.userservice.service.RbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RBAC权限管理服务实现类
 */
@Slf4j
@Service
public class RbacServiceImpl implements RbacService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Override
    public RolePermissionResult getUserRolesAndPermissions(Long userId) {
        try {
            // 查询用户角色(简化：假设用户只有一个主要角色)
            List<RolePO> roles = roleMapper.findRolesByUserId(userId);
            String roleName = roles.isEmpty() ? "USER" : roles.get(0).getName();
            
            // 查询用户权限
            List<PermissionPO> permissions = permissionMapper.findPermissionsByUserId(userId);
            List<String> permissionNames = permissions.stream()
                    .map(PermissionPO::getName)
                    .collect(Collectors.toList());
            
            return new RolePermissionResult(roleName, permissionNames);
        } catch (Exception e) {
            log.error("获取用户角色权限失败, userId: {}", userId, e);
            // 返回默认角色和空权限
            return new RolePermissionResult("USER", List.of());
        }
    }
    
    @Override
    @Transactional
    public boolean assignDefaultRole(Long userId) {
        try {
            // 查找默认角色
            RolePO userRole = roleMapper.findByName("USER");
            if (userRole == null) {
                log.error("默认角色USER不存在");
                return false;
            }
            
            // 检查用户是否已有该角色
            if (userRoleMapper.countByUserIdAndRoleId(userId, userRole.getId()) > 0) {
                log.info("用户{}已有USER角色", userId);
                return true;
            }
            
            // 分配角色
            UserRolePO userRolePO = new UserRolePO(userId, userRole.getId());
            int result = userRoleMapper.insert(userRolePO);
            
            log.info("为用户{}分配默认角色USER, 结果: {}", userId, result > 0 ? "成功" : "失败");
            return result > 0;
        } catch (Exception e) {
            log.error("分配默认角色失败, userId: {}", userId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean assignRoleToUser(Long userId, String roleName) {
        try {
            RolePO role = roleMapper.findByName(roleName);
            if (role == null) {
                log.error("角色{}不存在", roleName);
                return false;
            }
            
            // 检查用户是否已有该角色
            if (userRoleMapper.countByUserIdAndRoleId(userId, role.getId()) > 0) {
                log.info("用户{}已有角色{}", userId, roleName);
                return true;
            }
            
            UserRolePO userRolePO = new UserRolePO(userId, role.getId());
            int result = userRoleMapper.insert(userRolePO);
            
            log.info("为用户{}分配角色{}, 结果: {}", userId, roleName, result > 0 ? "成功" : "失败");
            return result > 0;
        } catch (Exception e) {
            log.error("分配角色失败, userId: {}, roleName: {}", userId, roleName, e);
            return false;
        }
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionName) {
        try {
            List<PermissionPO> permissions = permissionMapper.findPermissionsByUserId(userId);
            return permissions.stream()
                    .anyMatch(permission -> permission.getName().equals(permissionName));
        } catch (Exception e) {
            log.error("检查用户权限失败, userId: {}, permission: {}", userId, permissionName, e);
            return false;
        }
    }
    
    @Override
    public boolean hasRole(Long userId, String roleName) {
        try {
            List<RolePO> roles = roleMapper.findRolesByUserId(userId);
            return roles.stream()
                    .anyMatch(role -> role.getName().equals(roleName));
        } catch (Exception e) {
            log.error("检查用户角色失败, userId: {}, role: {}", userId, roleName, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public void initializeRbacData() {
        log.info("开始初始化RBAC基础数据...");
        
        try {
            // 1. 创建基础角色
            createBasicRoles();
            
            // 2. 创建基础权限
            createBasicPermissions();
            
            // 3. 分配角色权限
            assignRolePermissions();
            
            log.info("RBAC基础数据初始化完成");
        } catch (Exception e) {
            log.error("RBAC基础数据初始化失败", e);
        }
    }
    
    private void createBasicRoles() {
        String[] roleData = {
                "USER:普通用户,可以查看课程、选课、评论",
                "TEACHER:教师,可以创建和管理自己的课程",
                "ADMIN:管理员,拥有所有权限",
                "MODERATOR:版主,可以管理评论和内容"
        };
        
        for (String data : roleData) {
            String[] parts = data.split(":");
            String name = parts[0];
            String description = parts[1];
            
            if (roleMapper.findByName(name) == null) {
                RolePO role = new RolePO(name, description);
                roleMapper.insert(role);
                log.info("创建角色: {} - {}", name, description);
            }
        }
    }
    
    private void createBasicPermissions() {
        String[] permissionData = {
                "course:read:course:查看课程信息",
                "course:create:course:创建课程",
                "course:update:course:更新课程",
                "course:delete:course:删除课程",
                "comment:read:comment:查看评论",
                "comment:create:comment:发表评论",
                "comment:delete:comment:删除评论",
                "user:read:user:查看用户信息",
                "user:update:user:更新用户信息",
                "user:delete:user:删除用户",
                "enrollment:create:enrollment:选课",
                "enrollment:cancel:enrollment:退课"
        };
        
        for (String data : permissionData) {
            String[] parts = data.split(":");
            String name = parts[0] + "_" + parts[1]; // course_read, course_create等
            String action = parts[1];
            String resource = parts[2];
            String description = parts[3];
            
            if (permissionMapper.findByName(name) == null) {
                PermissionPO permission = new PermissionPO(name, action, resource, description);
                permissionMapper.insert(permission);
                log.info("创建权限: {} - {}", name, description);
            }
        }
    }
    
    private void assignRolePermissions() {
        // USER角色权限：基础的查看和创建权限
        assignPermissionsToRole("USER", new String[]{
                "course_read", "comment_read", "comment_create", 
                "user_read", "enrollment_create", "enrollment_cancel"
        });
        
        // TEACHER角色权限：包括USER权限 + 课程管理权限
        assignPermissionsToRole("TEACHER", new String[]{
                "course_read", "course_create", "course_update", "course_delete",
                "comment_read", "comment_create", "user_read", "user_update",
                "enrollment_create", "enrollment_cancel"
        });
        
        // MODERATOR角色权限：包括USER权限 + 内容管理权限
        assignPermissionsToRole("MODERATOR", new String[]{
                "course_read", "comment_read", "comment_create", "comment_delete",
                "user_read", "enrollment_create", "enrollment_cancel"
        });
        
        // ADMIN角色权限：所有权限
        assignPermissionsToRole("ADMIN", new String[]{
                "course_read", "course_create", "course_update", "course_delete",
                "comment_read", "comment_create", "comment_delete",
                "user_read", "user_update", "user_delete",
                "enrollment_create", "enrollment_cancel"
        });
    }
    
    private void assignPermissionsToRole(String roleName, String[] permissionNames) {
        RolePO role = roleMapper.findByName(roleName);
        if (role == null) {
            log.error("角色{}不存在", roleName);
            return;
        }
        
        for (String permissionName : permissionNames) {
            PermissionPO permission = permissionMapper.findByName(permissionName);
            if (permission == null) {
                log.error("权限{}不存在", permissionName);
                continue;
            }
            
            // 检查是否已经分配
            if (rolePermissionMapper.countByRoleIdAndPermissionId(role.getId(), permission.getId()) == 0) {
                RolePermissionPO rolePermission = new RolePermissionPO(role.getId(), permission.getId());
                rolePermissionMapper.insert(rolePermission);
                log.debug("为角色{}分配权限{}", roleName, permissionName);
            }
        }
    }
    
    // 系统启动时自动初始化数据
    @PostConstruct
    public void initOnStartup() {
        initializeRbacData();
    }
}
