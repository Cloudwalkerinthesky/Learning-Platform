package com.phantom.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public CommandLineRunner debugLoadBalancer(LoadBalancerClient loadBalancerClient) {
        return args -> {
            System.out.println("LoadBalancerClient: " + loadBalancerClient);
            ServiceInstance instance = loadBalancerClient.choose("user-service");
            System.out.println("Chosen instance for user-service: " + (instance != null ? instance.getUri() : "null"));
        };
    }
}