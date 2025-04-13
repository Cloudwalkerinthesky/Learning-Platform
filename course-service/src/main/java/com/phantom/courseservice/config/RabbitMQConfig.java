package com.phantom.courseservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String COURSE_ENROLLMENT_QUEUE="course.enrollment.queue";
    public static final String LEARNING_EXCHANGE="learning.exchange";
    public static final String ENROLLMENT_ROUTING_KEY="enrollment.key";
    @Bean
    public Queue courseEnrollmentQueue() {
        return new Queue(COURSE_ENROLLMENT_QUEUE, true); // 持久化队列
    }
    @Bean
    public TopicExchange learningExchange(){
        return new TopicExchange(LEARNING_EXCHANGE);
    }
    @Bean
    public Binding courseBinging(Queue courseEnrollmentQueue,TopicExchange learningExchange){
        return BindingBuilder.bind(courseEnrollmentQueue).to(learningExchange).with(ENROLLMENT_ROUTING_KEY);
    }

    //json转化器 将对象转化成json
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
