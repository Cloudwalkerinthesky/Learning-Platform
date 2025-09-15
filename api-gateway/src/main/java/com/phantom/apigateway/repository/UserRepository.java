package com.phantom.apigateway.repository;

import com.phantom.apigateway.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
    
    /**
     * 根据用户名查找用户
     */
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<User> findByUsername(String username);
    
    /**
     * 检查用户名是否已存在
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    Mono<Long> countByUsername(String username);
} 