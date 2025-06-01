package com.nt.tracker.service.impl;
import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.User;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.service.AuthService;
import com.nt.tracker.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;
    @Override
    public Result<String> register(User user) {
        try {
            // 密码md5加密
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            authMapper.addUser(user);

            // 确保获取到用户id
            if (user.getId() == null) {
                return Result.error("用户注册失败，未获取到用户ID");
            }

            // 生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            String token = JwtUtils.generateJwt(claims);

            return Result.success(token);
        } catch (DuplicateKeyException e) {
            // 捕获用户名重复的异常
            return Result.error("用户名已存在");
        }
    }

    @Override
    public User login(UserDTO user) {
        // 密码md5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        // 获取数据库中的用户
        return authMapper.getUserByNameAndPassword(user);
    }
}
