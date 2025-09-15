package com.phantom.learningservice.service;

import com.phantom.learningservice.constant.LearningConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.springframework.amqp.support.AmqpHeaders;
import com.rabbitmq.client.Channel;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentMessageConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // 消息统计计数器
    private static final AtomicLong totalMessages = new AtomicLong(0);
    private static final AtomicLong successMessages = new AtomicLong(0);
    private static final String REDIS_MESSAGE_STATS_KEY = "enrollment:message:stats";

    @RabbitListener(queues = LearningConstant.ENROLLMENT_QUEUE, ackMode = "MANUAL")
    public void handleEnrollmentEvent(@Payload Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        totalMessages.incrementAndGet();
        
        try {
            int userId = ((Number) message.get("userId")).intValue();
            int courseId = ((Number) message.get("courseId")).intValue();
            log.info("Received enrollment message for user {} and course {}", userId, courseId);

            // 业务逻辑处理...

            // 确认消息
            try {
                channel.basicAck(tag, false);
                log.info("Successfully acknowledged message for user {} and course {}", userId, courseId);
                
                // 记录消息处理成功
                successMessages.incrementAndGet();
                
                // 将统计数据保存到Redis，便于外部查询
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "total", totalMessages.get());
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "success", successMessages.get());
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "rate", 
                        String.format("%.2f%%", (double) successMessages.get() / totalMessages.get() * 100));
                redisTemplate.expire(REDIS_MESSAGE_STATS_KEY, 24, TimeUnit.HOURS);
                
                // 每处理1000条消息输出一次日志统计
                if (totalMessages.get() % 1000 == 0) {
                    log.info("消息处理统计 - 总消息数: {}, 成功数: {}, 成功率: {:.2f}%", 
                            totalMessages.get(), 
                            successMessages.get(),
                            (double) successMessages.get() / totalMessages.get() * 100);
                }
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
                
                // 将统计数据保存到Redis
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "total", totalMessages.get());
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "success", successMessages.get());
                redisTemplate.opsForHash().put(REDIS_MESSAGE_STATS_KEY, "rate", 
                        String.format("%.2f%%", (double) successMessages.get() / totalMessages.get() * 100));
            } catch (IOException ioException) {
                log.error("Failed to send NACK for tag {}, error: {}", tag, ioException.getMessage());
            }
            
            throw e; // 重新抛出异常，让RabbitMQ进行重试
        }
    }
    
    // 获取消息处理成功率统计
    public Map<Object, Object> getMessageStats() {
        return redisTemplate.opsForHash().entries(REDIS_MESSAGE_STATS_KEY);
    }
}
