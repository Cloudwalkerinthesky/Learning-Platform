package com.phantom.learningservice.config;

import com.phantom.learningservice.constant.LearningConstant;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//不是import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
//不是import org.springframework.messaging.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    @Bean(LearningConstant.ENROLLMENT_QUEUE)
    public Queue enrollmentQueue() {
        Map<String,Object> args=new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx.exchange");
        args.put("x-dead-letter-routing-key", "dlx.enrollment");


        return new Queue(LearningConstant.ENROLLMENT_QUEUE, true, false, false,args);
        // 参数解释：
        // 第一个参数是队列名；
        // 第二个参数是是否持久化（true 表示持久化队列）；
        // 第三个参数是是否排他（false 表示可以被多个连接访问）；
        // 第四个参数是是否自动删除（false 表示队列不会自动删除）。
    }

    @Bean("dlx.enrollment.queue")
    public Queue dlxEnrollmentQueue(){
        return new Queue("dlx.enrollment.queue");
    }

    @Bean(LearningConstant.LEARNING_EXCHANGE)
    public TopicExchange learningExchange() {
        return new TopicExchange("learning.exchange");
    }

    @Bean
    public Binding enrollmentBinding(@Qualifier(LearningConstant.ENROLLMENT_QUEUE) Queue queue,
                                     @Qualifier(LearningConstant.LEARNING_EXCHANGE) TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LearningConstant.ENROLLMENT_ROUTING_KEY);
    }

    @Bean
    public TopicExchange dlxExchange(){
        return new TopicExchange("dlx.exchange");
    }

    @Bean
    public Binding dlxBinding(@Qualifier("dlx.enrollment.queue") Queue dlxQueue,@Qualifier("dlxExchange")TopicExchange dlxExchange){
        return BindingBuilder.bind(dlxQueue).
                to(dlxExchange).
                with("dlx.enrollment");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //作为生产者的json转换器
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template=new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    //作为消费者的json转换器
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory=new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());// 为所有 @RabbitListener 设置 JSON 转换器
        return factory;
    }


    //course-service和learning-service的服务间通信
    @Bean
    public Queue courseStatusQueue(){
        return new Queue("course.status.queue",true);
    }
    @Bean
    public DirectExchange courseStatusExchange(){
        return new DirectExchange("course.status.exchange");
    }
    @Bean
    public Binding binding(Queue courseStatusQueue, DirectExchange courseStatusExchange){
        return BindingBuilder.bind(courseStatusQueue).to(courseStatusExchange).with("course.status.update");
    }




}
