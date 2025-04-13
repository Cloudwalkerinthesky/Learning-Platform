package com.phantom.userservice.mapper;

import com.phantom.userservice.bean.po.UserPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    @Select("select id,username,account from tb_user where id=#{id}")
    UserPO getUserPoById(@Param("id")Integer id);

    @Select("SELECT COUNT(*) FROM tb_user WHERE username=#{username}")
    boolean existsByUsername(String username);

    @Insert("INSERT INTO tb_user(username,password,email,account)"+"VALUES (#{username},#{password},#{email},#{account})")
    void insert(UserPO userPO);

    @Select("select id,username,password,account,email,role from tb_user where username=#{username}")
    UserPO getUserByUsername(@Param("username")String username);

}
