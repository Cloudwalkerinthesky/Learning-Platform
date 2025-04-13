package com.phantom.learningservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ObjectInputFilter;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){
        Config config=new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.150.101:6379");
        return Redisson.create(config);
    }
}
