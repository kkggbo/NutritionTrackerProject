package com.nt.tracker.service.impl;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.dto.UserProfile;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public Result<String> setUserProfile(UserProfileDTO userProfileDTO) {
        Long userId = userProfileDTO.getUserId();
        // 检查用户是否存在
        if (getUserById(userId) == null) {
            return Result.error("用户不存在");
        }

        // 检查用户是否已经设置过信息
        if (userMapper.getUserProfile(userId) != null) {
            return Result.error("用户信息已存在");
        }

        // 创建用户信息对象
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userProfileDTO, userProfile);

        // 计算并设置用户BMI值
        userProfile.setBmi(userProfile.getWeight() / Math.pow(userProfile.getHeight() / 100, 2));

        // 计算并设置用户BMR值 (Mifflin-St Jeor 公式)
        if(userProfile.getGender() == 1){
            // 男性BMR = 10 × 体重(kg) + 6.25 × 身高(cm) – 5 × 年龄 + 5
            userProfile.setBmr((int) Math.round(10 * userProfile.getWeight() + 6.25 * userProfile.getHeight() - 5 * userProfile.getAge() + 5));

        } else {
            // 女性BMR = 10 × 体重(kg) + 6.25 × 身高(cm) – 5 × 年龄 – 161
            userProfile.setBmr((int) Math.round(10 * userProfile.getWeight() + 6.25 * userProfile.getHeight() - 5 * userProfile.getAge() - 161));
        }

        // 计算每日总能量消耗（TDEE）
        double tdee = userProfile.getBmr() * userProfile.getActivityLevel();

        // 设定热量目标
        if (userProfile.getGoal() == 1){
            // 用户目标增肌
            userProfile.setDailyCalories((int) Math.round(tdee * 1.15));
        } else {
            // 用户目标减脂
            userProfile.setDailyCalories((int) Math.round(tdee * 0.85));
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
