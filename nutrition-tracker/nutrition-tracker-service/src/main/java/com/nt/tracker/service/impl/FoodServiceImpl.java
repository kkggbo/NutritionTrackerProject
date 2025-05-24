package com.nt.tracker.service.impl;
import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.AuthService;
import com.nt.tracker.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> addFood(FoodDTO food) {
        // 检查是食物否已存在
        if (foodMapper.getFoodByName(food.getName()) != null) {
            return Result.error("食物已存在");
        }
        foodMapper.addFood(food);
        return Result.success();
    }

    @Override
    public Result<List<FoodVO>> getFoodInventory(Long userId) {
        List<FoodVO> foodInventory= foodMapper.getFoodInventory(userId);
        return Result.success(foodInventory);
    }

    @Override
    public Result<String> intake(IntakeDTO intakeInfo) {
        if (userMapper.getUserProfile(intakeInfo.getUserId()) == null) {
            return Result.error("此用户不存在");
        }
        if (foodMapper.getFoodById(intakeInfo.getFoodId()) == null) {
            return Result.error("食物不存在");
        }
        foodMapper.addIntake(intakeInfo);
        return Result.success();
    }

    @Override
    public Result<IntakeDetailVO> getTodayIntakeDetails(Long userId, LocalDate date) {
        // 获取用户当天摄入的食物id和重量信息
        List<IntakeDTO> todayIntakeDetails = foodMapper.getIntakesByIdAndDate(userId, date);
        if (todayIntakeDetails.isEmpty()){
            return Result.error("用户今天没有摄入食物记录");
        }

        // 查询并计算用户当天摄入的能量、碳水化合物、蛋白质和脂肪
        IntakeDetailVO intakeDetail = new IntakeDetailVO();
        double totalCalories = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;
        for (IntakeDTO detail : todayIntakeDetails) {
            FoodVO food = foodMapper.getFoodById(detail.getFoodId());
            intakeDetail.addFood(food);
            totalCalories += food.getCaloriesPer100g() * detail.getWeight() / 100;
            totalCarbs += food.getCarbsPer100g() * detail.getWeight() / 100;
            totalProtein += food.getProteinPer100g() * detail.getWeight() / 100;
            totalFat += food.getFatPer100g() * detail.getWeight() / 100;
        }

        // 添加日期信息
        intakeDetail.setDate(date);

        // 添加总能量、碳水化合物、蛋白质和脂肪信息
        intakeDetail.setOverallInfo(totalCalories, totalCarbs, totalProtein, totalFat);
        return Result.success(intakeDetail);
    }
}
