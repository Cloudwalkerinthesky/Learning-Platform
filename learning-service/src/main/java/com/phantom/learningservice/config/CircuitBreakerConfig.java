package com.phantom.learningservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class CircuitBreakerConfig {
    @Bean
    public CircuitBreaker enrollCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry){
        return circuitBreakerRegistry.circuitBreaker("enrollService");
    }
    @Autowired
    private CircuitBreaker enrollCircuitBreaker;

    @PostConstruct
    public void configureCircuitBreakerListener(){
        enrollCircuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    log.info("Circuit breaker '{}' changed state from {} to {}",
                            event.getCircuitBreakerName(),
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState());
                });
    }
}
