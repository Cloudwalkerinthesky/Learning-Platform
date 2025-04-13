package com.phantom.courseservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class CourseServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;





}
