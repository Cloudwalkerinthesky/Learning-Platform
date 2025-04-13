package com.phantom.learningservice.feign;


import com.phantom.common.bean.dto.UserBaseInfoDTO;
import com.phantom.common.bean.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("user/baseInfo/{id}")
    R<UserBaseInfoDTO> getUserBaseInfoById(@PathVariable("id")Integer id);

}

