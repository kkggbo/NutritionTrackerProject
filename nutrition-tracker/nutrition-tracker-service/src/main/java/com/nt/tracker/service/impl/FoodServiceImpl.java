package com.nt.tracker.service.impl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.MealUpdateRequestDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.po.MealFood;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.AuthService;
import com.nt.tracker.service.FoodService;
import com.nt.tracker.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     *  添加食物
     * @param food
     * @return
     */
    @Override
    public Result<String> addFood(FoodDTO food) {
        // 检查是食物否已存在
        if (foodMapper.getFoodByName(food.getName()) != null) {
            return Result.error("食物已存在");
        }
        foodMapper.addFood(food);
        return Result.success();
    }

    /**
     *  获取用户添加食物库存列表
     * @param userId
     * @return
     */
    @Override
    public Result<List<FoodVO>> getFoodInventory(Long userId) {
        List<FoodVO> foodInventory= foodMapper.getFoodInventory(userId);
        return Result.success(foodInventory);
    }

    /**
     *  添加用户食物摄入信息
     * @param intakeInfo
     * @return
     */
    @Override
    public Result<String> intake(IntakeDTO intakeInfo) {
        // 检查参数是否合法
        if (intakeInfo == null || intakeInfo.getWeight() <= 0) {
            return Result.error("参数错误：食物信息不能为空且重量必须大于0");
        }

        // 获取用户Id和日期
        Long userId = UserThreadLocal.getUserId();
        LocalDate today = LocalDate.now();

        // 检查食物是否存在
        if (foodMapper.getFoodById(intakeInfo.getFoodId()) == null) {
            return Result.error("食物不存在");
        }

        // 检查用户是否已经添加过该食物
        IntakeDTO intakeFood = foodMapper.getIntakeFood(userId, intakeInfo.getMealType(), today, intakeInfo.getFoodId());
        if ( intakeFood != null) {
            // 用户已添加过食物，更新重量
            foodMapper.updateMealFoodWeight(userId, intakeInfo.getMealType(), today, intakeInfo.getFoodId(), intakeInfo.getWeight()+ intakeFood.getWeight());
            return Result.success();
        }

        // 添加新食物
        foodMapper.addIntake(intakeInfo, userId);
        return Result.success();
    }

    /**
     * 获取用户当天的摄入详情
     * @param userId
     * @param date
     * @return
     */
    @Override
    public Result<IntakeDetailVO> getTodayIntakeDetails(Long userId, LocalDate date) {
        // TODO 获取用户当天摄入的食物id和重量信息
//        List<IntakeDTO> todayIntakeDetails = foodMapper.getIntakesByIdAndDate(userId, date);
//        if (todayIntakeDetails.isEmpty()){
//            return Result.error("用户今天没有摄入食物记录");
//        }
//
//        // 查询并计算用户当天摄入的能量、碳水化合物、蛋白质和脂肪
//        IntakeDetailVO intakeDetail = new IntakeDetailVO();
//        double totalCalories = 0;
//        double totalCarbs = 0;
//        double totalProtein = 0;
//        double totalFat = 0;
//        for (IntakeDTO detail : todayIntakeDetails) {
//            FoodVO food = foodMapper.getFoodById(detail.getFoodId());
//            intakeDetail.addFood(food);
//            totalCalories += food.getCaloriesPer100g() * detail.getWeight() / 100;
//            totalCarbs += food.getCarbsPer100g() * detail.getWeight() / 100;
//            totalProtein += food.getProteinPer100g() * detail.getWeight() / 100;
//            totalFat += food.getFatPer100g() * detail.getWeight() / 100;
//        }
//
//        // 添加日期信息
//        intakeDetail.setDate(date);
//
//        // 添加总能量、碳水化合物、蛋白质和脂肪信息
//        intakeDetail.setOverallInfo(totalCalories, totalCarbs, totalProtein, totalFat);
        return Result.success();
    }

    /**
     *  获取食物详情
     * @param foodId
     * @return
     */
    @Override
    public Result<FoodVO> getFoodDetail(Long foodId) {
         FoodVO food = foodMapper.getFoodById(foodId);
         return food == null ? Result.error("食物不存在") : Result.success(food);
    }

    @Override
    public List<FoodVO> getFoods(int page, int size, String name) {
        // 开始分页查询
        PageHelper.startPage(page, size);

        // 根据名称查询食物
        Page<FoodVO> foods = foodMapper.foodQuery(name);
        return foods.getResult();
    }

    @Override
    public Result getMealInfo(int mealType, LocalDate date) {
        // 获取当前用户id
        Long userId = UserThreadLocal.getUserId();

        // 查询用户的饮食信息
        List<MealFood> foods = foodMapper.getMealFoods(userId, mealType, date);

        // 返回结果
        return Result.success(foods);
    }

    @Override
    public Result updateMealFoods(MealUpdateRequestDTO request) {
        LocalDate date = request.getDate();
        int mealType = request.getMealType();
        Long userId = UserThreadLocal.getUserId();

        // 获取需要被修改和删除的食物列表
        List<MealFood> foods = request.getFoods();
        List<MealFood> foodsToUpdate = new ArrayList<>();
        List<Long> deletedFoodIds = new ArrayList<>();

        for (MealFood food : foods) {
            if (food.getWeight() != 0) {
                foodsToUpdate.add(food);
            } else {
                deletedFoodIds.add(food.getId());
            }
        }

        // 批量修改、删除数据
        if (!foodsToUpdate.isEmpty()) {
            for (MealFood food : foodsToUpdate){
                foodMapper.updateMealFoods(userId, mealType, date, food);
            }
        }

        if (!deletedFoodIds.isEmpty()) {
            foodMapper.deleteMealFoods(userId, mealType, date, deletedFoodIds);
        }

        return Result.success();
    }
}
