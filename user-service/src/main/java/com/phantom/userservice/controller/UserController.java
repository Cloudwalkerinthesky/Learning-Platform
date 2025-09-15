package com.phantom.userservice.controller;

import com.phantom.common.context.UserContextHolder;
import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.vo.R;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/test/{id}")
    public String test(@PathVariable Integer id){
        System.out.println(id);
        return id.toString();
    }

    @GetMapping("/favor/{id}")
    public R getUserFavorById(@PathVariable Integer id){
        // 从ThreadLocal获取当前用户信息
        com.phantom.common.bean.dto.UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            logger.info("用户 {} 正在查看用户 {} 的收藏信息", currentUser.getUsername(), id);
        }
        
        UserFavorVO vo = userService.getUserFavorById(id);
        if(vo != null){
            return R.ok(vo);
        } else {
            return R.failed("用户不存在");
        }
    }

    @GetMapping("/baseInfo/{id}")
    public R getUserBaseInfoById(@PathVariable Integer id){
        // 从ThreadLocal获取当前用户信息
        com.phantom.common.bean.dto.UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            logger.info("用户 {} 正在查看用户 {} 的基本信息", currentUser.getUsername(), id);
            
            // 权限检查：只能查看自己的信息
            if (!id.equals(currentUser.getId())) {
                return R.failed("无权限查看其他用户信息");
            }
        }

        long startTime = System.currentTimeMillis();
        UserBaseInfoDTO dto = userService.getUserBaseInfoById(id);
        long endTime = System.currentTimeMillis();
        logger.info("getUserBaseInfoById响应时间: {} ms", (endTime - startTime));
        
        if(dto != null){
            return R.ok(dto);
        } else {
            return R.failed("用户不存在");
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public R getCurrentUser() {
        com.phantom.common.bean.dto.UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            logger.info("获取当前用户信息: {}", currentUser.getUsername());
            return R.ok(currentUser);
        } else {
            return R.failed("未登录");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public R updateUserInfo(@RequestBody com.phantom.common.bean.dto.UserBaseInfoDTO userInfo) {
        com.phantom.common.bean.dto.UserBaseInfoDTO currentUser = UserContextHolder.getUser();
        if (currentUser != null) {
            logger.info("用户 {} 正在更新个人信息", currentUser.getUsername());
            
            // 确保只能更新自己的信息
            userInfo.setId(currentUser.getId());
            userService.updateUserBaseInfo(userInfo);
            return R.ok("更新成功");
        } else {
            return R.failed("未登录");
        }
    }
}
