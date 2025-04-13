package com.phantom.commentservice;

import com.phantom.commentservice.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentServiceApplicationTests {

    @Autowired
    CommentMapper commentMapper;
    @Test
    void contextLoads() {
    }


}
