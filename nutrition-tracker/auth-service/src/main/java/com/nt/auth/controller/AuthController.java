package com.nt.auth.controller;


import com.nt.auth.domain.dto.UserDTO;
import com.nt.auth.domain.po.User;
import com.nt.auth.service.AuthService;
import com.nt.common.Result;
import com.nt.common.utils.JwtUtils;
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

    /**
     * 注册
     * @param userDto
     * @return
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return authService.register(user);
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO user) {
        System.out.println("用户登录: "+user);

        User u = authService.login(user);

        // 是否登陆成功
        if(u == null){
            return Result.error("用户名或密码错误");
        }

        // 生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", u.getId());
        String token = JwtUtils.generateJwt(claims, user.getRememberMe());

        // 令牌中已经包含了当前用户的id
        // 返回token
        return Result.success(token);
    }

}
