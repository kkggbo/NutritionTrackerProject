package com.nt.tracker.service.impl;
import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;
    @Override
    public Result<String> register(String username, String password) {
        //
        try {
            // 密码md5加密
            String encodedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
            authMapper.addUser(username, encodedPassword);
            return Result.success();
        } catch (DuplicateKeyException e) {
            // 捕获用户名重复的异常
            return Result.error("用户名已存在");
        }

    }

    @Override
    public UserDTO login(UserDTO user) {
        // 密码md5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        // 获取数据库中的用户
        return authMapper.getUserByNameAndPassword(user);
    }
}
