package com.phantom.userservice.mapper;

import com.phantom.common.bean.po.rbac.RolePermissionPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface RolePermissionMapper {
    
    /**
     * 为角色分配权限
     */
    @Insert("INSERT INTO role_permissions (role_id, permission_id, created_time) " +
            "VALUES (#{roleId}, #{permissionId}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RolePermissionPO rolePermission);
    
    /**
     * 删除角色的权限
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 删除角色的所有权限
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 查询角色的所有权限关联
     */
    @Select("SELECT * FROM role_permissions WHERE role_id = #{roleId}")
    List<RolePermissionPO> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 检查角色是否有指定权限
     */
    @Select("SELECT COUNT(*) FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int countByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
