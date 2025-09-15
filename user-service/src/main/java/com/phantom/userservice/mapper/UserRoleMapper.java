package com.phantom.userservice.mapper;

import com.phantom.common.bean.po.rbac.UserRolePO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper {
    
    /**
     * 为用户分配角色
     */
    @Insert("INSERT INTO user_roles (user_id, role_id, created_time) " +
            "VALUES (#{userId}, #{roleId}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserRolePO userRole);
    
    /**
     * 删除用户的角色
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 删除用户的所有角色
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的所有角色关联
     */
    @Select("SELECT * FROM user_roles WHERE user_id = #{userId}")
    List<UserRolePO> findByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否有指定角色
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
