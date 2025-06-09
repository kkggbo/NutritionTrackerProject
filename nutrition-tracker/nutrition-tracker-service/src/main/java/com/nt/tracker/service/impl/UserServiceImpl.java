package com.nt.tracker.service.impl;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.dto.UserProfile;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.po.IntakePO;
import com.nt.tracker.domain.vo.DiaryVO;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.UserService;
import com.nt.tracker.utils.RedisUtils;
import com.nt.tracker.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.nt.tracker.common.Constants.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Result<String> setUserProfile(UserProfileDTO userProfileDTO) {
        Long userId = UserThreadLocal .getUserId();
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
        userProfile.setUserId(userId);

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
    public Result<UserProfileDTO> getUserProfile() {
        Long userId = UserThreadLocal.getUserId();
        UserProfileDTO userProfile = userMapper.getUserProfile(userId);
        return userProfile == null ? Result.error("用户不存在") : Result.success(userProfile);
    }

    @Override
    public Result<DiaryVO> getDiary() {
        Long userId = UserThreadLocal.getUserId();
        LocalDate today = LocalDate.now();
        DiaryVO diary;

        // 尝试从Redis缓存中获取
        String dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = REDIS_KEY_DIARY + userId + "::" + dateStr;
        diary = redisUtils.get(key, DiaryVO.class);

        // 缓存中有结果则直接返回
        if (diary != null) {
            return Result.success(diary);
        }
        diary = new DiaryVO();

        // 缓存中没有结果则从数据库中查询
        // 获取当前用户每日热量获取目标
        int goalCalories = userMapper.getDailyCalories(userId);

        // 计算用户各营养物摄入目标 (暂时仅简单根据碳水供能50%，蛋白质供能20%，脂肪供能30%计算)
        int goalCarb = (int) Math.round(goalCalories * 0.5 / 4);    // 碳水供能50%，每克4卡
        int goalProtein = (int) Math.round(goalCalories * 0.2 / 4); //  蛋白质供能20%，每克4卡
        int goalFat = (int) Math.round(goalCalories * 0.3 / 9);     //  脂肪供能30%，每克9卡


        // 获取用户今日每餐已摄入的食物信息
        List<IntakePO> todayIntakeDetails = foodMapper.getIntakesByIdAndDate(userId, today);
        if (todayIntakeDetails == null) {
            todayIntakeDetails = new ArrayList<>();
        }

        // 计算今日各营养物总摄入量
        // 声明Meal对象
        DiaryVO.Meal breakFast = new DiaryVO.Meal(MEAL_BREAKFAST, 0);
        DiaryVO.Meal lunch = new DiaryVO.Meal(MEAL_LUNCH, 0);
        DiaryVO.Meal  dinner = new DiaryVO.Meal(MEAL_DINNER, 0);
        DiaryVO.Meal snack = new DiaryVO.Meal(MEAL_SNACK, 0);

        int  totalCalories = 0;
        int realCarb = 0;
        int realProtein = 0;
        int realFat = 0;

        // 遍历todayIntakeDetails计算进入摄入的总热量、总碳水、总蛋白质、总脂肪含量和每一餐的总热量
         for (IntakePO intake : todayIntakeDetails) {
             switch (intake.getMealType()) {
                 case MEAL_BREAKFAST:
                     breakFast.setCalories(breakFast.getCalories() + calculateWeight(intake.getCaloriesPer100g(), intake.getWeight()));
                     break;
                 case  MEAL_LUNCH:
                     lunch.setCalories(lunch.getCalories() + calculateWeight(intake.getCaloriesPer100g(), intake.getWeight()));
                     break;
                 case MEAL_DINNER:
                     dinner.setCalories(dinner.getCalories() + calculateWeight(intake.getCaloriesPer100g(), intake.getWeight()));
                     break;
                 case MEAL_SNACK:
                     snack.setCalories(snack.getCalories() + calculateWeight(intake.getCaloriesPer100g(), intake.getWeight()));
                     break;
                 default:
                     log.warn("Unknown mealType: " + intake.getMealType());
                     break;
             }

             totalCalories  += (calculateWeight(intake.getCaloriesPer100g(), intake.getWeight()));
             realCarb       += calculateWeight(intake.getCarbsPer100g(), intake.getWeight());
             realProtein    += calculateWeight(intake.getProteinPer100g(), intake.getWeight());
             realFat        += calculateWeight(intake.getFatPer100g(), intake.getWeight());
         }


        // 把数据放入diary对象中
        diary.setGoalCalories(goalCalories);
        diary.setTotalCalories(totalCalories);
        List<DiaryVO.Macro> macros = Arrays.asList(
                new DiaryVO.Macro(MACRO_CARBOHYDRATE, realCarb, goalCarb),
                new DiaryVO.Macro(MACRO_PROTEIN, realProtein, goalProtein),
                new DiaryVO.Macro(MACRO_FAT, realFat, goalFat)
        );
        diary.setMacros(macros);

        List<DiaryVO.Meal> meals = new ArrayList<>();
        Collections.addAll(meals, breakFast, lunch, dinner, snack);
        diary.setMeals(meals);

        // 把结果加入到缓存中
        redisUtils.set(key, diary, CACHE_TIME_LONG, TimeUnit.MINUTES);

        //  返回数据
        return Result.success(diary);
    }

    public static int calculateWeight(Double weightPer100g, Double weight){
        return (int) Math.round(weightPer100g * weight / 100.0);
    }

    private UserDTO getUserById(Long id) {
        return userMapper.getUserById(id);
    }

}

