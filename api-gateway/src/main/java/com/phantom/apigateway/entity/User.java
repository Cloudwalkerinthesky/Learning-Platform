package com.phantom.apigateway.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("users")
public class User {
    @Id
    private Integer id;
    
    @Column("username")
    private String username;
    
    @Column("password")
    private String password;
    
    @Column("email")
    private String email;
    
    @Column("account")
    private String account;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
} 