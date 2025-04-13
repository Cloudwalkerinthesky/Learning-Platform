package com.phantom.learningservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan("com.phantom.learningservice.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class LearningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningServiceApplication.class, args);
    }

}
