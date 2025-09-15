package com.phantom.learningservice.controller;

import com.phantom.common.bean.vo.R;
import com.phantom.learningservice.service.EnrollmentMessageConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {

    private final EnrollmentMessageConsumer enrollmentMessageConsumer;

    @GetMapping("/message-stats")
    public R<Map<Object, Object>> getMessageStats() {
        try {
            Map<Object, Object> stats = enrollmentMessageConsumer.getMessageStats();
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取消息统计数据失败", e);
            return R.failed("获取消息统计数据失败: " + e.getMessage());
        }
    }
} 