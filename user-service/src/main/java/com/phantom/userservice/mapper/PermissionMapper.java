package com.phantom.userservice.mapper;

import com.phantom.common.bean.po.rbac.PermissionPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface PermissionMapper {
    
    /**
     * 根据权限名称查询权限
     */
    @Select("SELECT * FROM permissions WHERE name = #{name}")
    PermissionPO findByName(@Param("name") String name);
    
    /**
     * 根据用户ID查询用户的权限
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<PermissionPO> findPermissionsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询权限
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<PermissionPO> findPermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 插入权限
     */
    @Insert("INSERT INTO permissions (name, action, resource, description, created_time, updated_time) " +
            "VALUES (#{name}, #{action}, #{resource}, #{description}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PermissionPO permission);
    
    /**
     * 查询所有权限
     */
    @Select("SELECT * FROM permissions ORDER BY id")
    List<PermissionPO> findAll();
}
