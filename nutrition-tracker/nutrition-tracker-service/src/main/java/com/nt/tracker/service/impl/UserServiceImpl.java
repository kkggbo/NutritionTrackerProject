package com.nt.tracker.service.impl;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> setUserProfile(UserProfileDTO userProfile) {
        // 检查用户是否存在
        if (getUserById(userProfile.getUserId()) == null) {
            return Result.error("用户不存在");
        }

        // 检查用户是否已经设置过信息
        if (getUserProfile(userProfile.getUserId()) != null) {
            return Result.error("用户信息已存在");
        }

        // 插入用户信息
        userMapper.insertUserProfile(userProfile);
        return Result.success();
    }

    @Override
    public Result<UserProfileDTO> getUserProfile(Long userId) {
        UserProfileDTO userProfile = userMapper.getUserProfile(userId);
        return userProfile == null ? Result.error("用户不存在") : Result.success(userProfile);
    }

    private UserDTO getUserById(Long id) {
        return userMapper.getUserById(id);
    }
}
