package com.phantom.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RouteConfig {

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("baidu", r -> r.path("/baidu/**")
//                        .filters(f -> f.rewritePath("/baidu/?(?<segment>.*)", "/${segment}")
//                                .redirect(302, "https://www.baidu.com"))
//                        .uri("https://www.baidu.com"))
//                .build();
//    }
}
