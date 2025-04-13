package com.phantom.userservice.controller;

import com.phantom.userservice.bean.dto.LoginDTO;
import com.phantom.userservice.bean.dto.RegisterDTO;
import com.phantom.userservice.bean.dto.UserBaseInfoDTO;
import com.phantom.userservice.bean.vo.R;
import com.phantom.userservice.bean.vo.UserFavorVO;
import com.phantom.userservice.security.JwtUtils;
import com.phantom.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/test/{id}")
    public String test(@PathVariable Integer id){
        System.out.println(id);
        return id.toString();
    }

    @GetMapping("/favor/{id}")
    public R getUserFavorById(@PathVariable Integer id){
        UserFavorVO vo=userService.getUserFavorById(id);
        if(vo!=null){
            return R.ok(vo);
        }
        else
            return R.failed("用户不存在");
    }

    @GetMapping("/baseInfo/{id}")
    @PreAuthorize("hasRole('USER')")
    public R getUserBaseInfoById(@PathVariable Integer id,HttpServletRequest request){
        String token = jwtUtils.extractToken(request);
        if(token==null){
            return R.failed("No token found");
        }
        Integer userId=jwtUtils.getUserIdFromToken(token);
        if(!id.equals(userId)){
            return R.failed("Access denied. You can only view your own information.");
        }

//        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername=authentication.getName();
//        UserBaseInfoDTO currentUser=userService.getUserBaseInfoByUsername(currentUsername);
//        if(currentUser!=null||currentUser.getId().equals(id)){
//            return R.failed("Access denied. You can only view your own information");
//        }
        UserBaseInfoDTO dto=userService.getUserBaseInfoById(id);
        if(dto!=null){
            return R.ok(dto);
        }
        else
            return R.failed("User doesn't exist");
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterDTO registerDTO){
        try {
            userService.register(registerDTO);
            return R.ok("Registration successful");
        }catch (RuntimeException e){
            return R.failed(e.getMessage());
        }
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody LoginDTO loginDTO){
        try{
            String token=userService.login(loginDTO);
            return R.ok(token);
        }catch (Exception e){
            return R.failed(e.getMessage());
        }

    }
//    @PostMapping("/logout")
//    public R logout(HttpServletRequest request){
//        String token=jwtUtils.extractToken(request);
//        redisTemplate.opsForValue().set(token,"invalid",jwtUtils.getExpiration(token), TimeUnit.MICROSECONDS);
//        return R.ok();
//    }





}
