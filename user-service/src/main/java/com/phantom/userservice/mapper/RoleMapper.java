package com.phantom.userservice.mapper;

import com.phantom.common.bean.po.rbac.RolePO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {
    
    /**
     * 根据角色名称查询角色
     */
    @Select("SELECT * FROM roles WHERE name = #{name}")
    RolePO findByName(@Param("name") String name);
    
    /**
     * 根据用户ID查询用户的角色
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<RolePO> findRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 插入角色
     */
    @Insert("INSERT INTO roles (name, description, created_time, updated_time) " +
            "VALUES (#{name}, #{description}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RolePO role);
    
    /**
     * 查询所有角色
     */
    @Select("SELECT * FROM roles ORDER BY id")
    List<RolePO> findAll();
}
