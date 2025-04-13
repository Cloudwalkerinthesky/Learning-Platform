package com.phantom.learningservice.service;

import com.phantom.learningservice.constant.LearningConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.springframework.amqp.support.AmqpHeaders;
import com.rabbitmq.client.Channel;

import java.util.Map;

@Service
@Slf4j
public class EnrollmentMessageConsumer {

    @RabbitListener(queues = LearningConstant.ENROLLMENT_QUEUE, ackMode = "MANUAL")
    public void handleEnrollmentEvent(@Payload Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            int userId = ((Number) message.get("userId")).intValue();
            int courseId = ((Number) message.get("courseId")).intValue();
            log.info("Received enrollment message for user {} and course {}", userId, courseId);

            // 业务逻辑处理...

            // 确认消息
            try {
                channel.basicAck(tag, false);
                log.info("Successfully acknowledged message for user {} and course {}", userId, courseId);
            } catch (IOException e) {
                log.error("Failed to send ACK for tag {}, error: {}", tag, e.getMessage());
                // 此处可以选择重新抛出异常或处理连接问题
                throw new RuntimeException("ACK failed", e);
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            try {
                channel.basicNack(tag, false, false);
                log.info("Message with tag {} has been Nacked", tag);
            } catch (IOException ioException) {
                log.error("Failed to send NACK for tag {}, error: {}", tag, ioException.getMessage());
            }
        }
//        try {
//            int userId = ((Number) message.get("userId")).intValue();
//            int courseId = ((Number) message.get("courseId")).intValue();
//            log.info("Processing user {} and course {}", userId, courseId);
//
//            // 模拟业务逻辑（替换为实际代码）
//
//
//            channel.basicAck(tag, false);
//            log.info("ACK sent");
//        } catch (Exception e) {
//            log.error("处理失败，发送NACK", e);
//            channel.basicNack(tag, false, false); // 不重新入队，进入死信队列
//        }
    }
}
