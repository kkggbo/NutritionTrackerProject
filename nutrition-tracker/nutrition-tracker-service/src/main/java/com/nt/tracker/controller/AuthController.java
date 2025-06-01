package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.User;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.service.AuthService;
import com.nt.tracker.service.impl.AuthServiceImpl;
import com.nt.tracker.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return authService.register(user);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO user) {
        User u = authService.login(user);

        // 是否登陆成功
        if(u == null){
            return Result.error("用户名或密码错误");
        }

        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", u.getId());
        String token = JwtUtils.generateJwt(claims);

        // 令牌中已经包含了当前用户的id
        // 返回token
        return Result.success(token);
    }

}
