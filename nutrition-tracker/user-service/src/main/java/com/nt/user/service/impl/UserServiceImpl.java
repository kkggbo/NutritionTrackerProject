package com.nt.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.nt.common.Result;
import com.nt.common.utils.RedisUtils;
import com.nt.common.utils.UserThreadLocal;
import com.nt.user.client.FoodClient;
import com.nt.user.domain.dto.UserDTO;
import com.nt.user.domain.dto.UserProfileDTO;
import com.nt.user.domain.po.IntakePO;
import com.nt.user.domain.po.UserProfile;
import com.nt.user.domain.vo.DiaryVO;
import com.nt.user.mapper.UserMapper;
import com.nt.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nt.common.Constants.*;


@Service
@Slf4j
// 通过构造函数注入restTemplate
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

//    // 用final修饰以保证被lombok通过构造函数注入
//    private final RestTemplate restTemplate;
//
//    private final DiscoveryClient discoveryClient;

    private final FoodClient foodClient;

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private FoodMapper foodMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 设置用户信息
     * @param dto
     * @return
     */
    @Override
    public Result<String> setUserProfile(UserProfileDTO dto) {
        Long userId = UserThreadLocal.getUserId();

        // 检查用户是否存在
        if (getUserById(userId) == null) {
            return Result.error("用户不存在");
        }

        // 检查是否已设置过信息
        if (userMapper.getUserProfile(userId) != null) {
            return Result.error("用户信息已存在");
        }

        UserProfile userProfile = buildUserProfile(dto, userId);
        userMapper.insertUserProfile(userProfile);

        return Result.success();
    }


    /**
     * 获取用户信息
     * @return
     */
    @Override
    public Result<UserProfileDTO> getUserProfile() {
        Long userId = UserThreadLocal.getUserId();
        UserProfileDTO userProfile = userMapper.getUserProfile(userId);
        return userProfile == null ? Result.error("用户不存在") : Result.success(userProfile);
    }

    /**
     * 更新用户信息
     * @param dto
     * @return
     */
    @Override
    public Result<String> updateUserProfile(UserProfileDTO dto) {
        Long userId = UserThreadLocal.getUserId();

        // 检查用户是否存在
        if (getUserById(userId) == null) {
            return Result.error("用户不存在");
        }

        // 检查用户信息是否存在
        if (userMapper.getUserProfile(userId) == null) {
            return Result.error("用户信息不存在");
        }

        UserProfile userProfile = buildUserProfile(dto, userId);
        userMapper.updateUserProfile(userProfile);

        return Result.success();
    }




    // TODO 远程调用food service
    /**
     * 获取用户日食信息
     * @return
     */
    @Override
    public Result<DiaryVO> getDiary() {
        Long userId = UserThreadLocal.getUserId();
        LocalDate today = LocalDate.now();

        DiaryVO diary;

        // 尝试从Redis缓存中获取
        String dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = REDIS_KEY_DIARY + userId + "::" + dateStr;
        /*
        diary = redisUtils.get(key, DiaryVO.class);

        // 缓存中有结果则直接返回
        if (diary != null) {
            return Result.success(diary);
        }
        */
        diary = new DiaryVO();

        // 缓存中没有结果则从数据库中查询
        // 获取当前用户每日热量获取目标
        int goalCalories = userMapper.getDailyCalories(userId);

        // 计算用户各营养物摄入目标 (暂时仅简单根据碳水供能50%，蛋白质供能20%，脂肪供能30%计算)
        int goalCarb = (int) Math.round(goalCalories * 0.5 / 4);    // 碳水供能50%，每克4卡
        int goalProtein = (int) Math.round(goalCalories * 0.2 / 4); //  蛋白质供能20%，每克4卡
        int goalFat = (int) Math.round(goalCalories * 0.3 / 9);     //  脂肪供能30%，每克9卡


        // 获取用户今日每餐已摄入的食物信息
        // List<IntakePO> todayIntakeDetails = foodMapper.getIntakesByIdAndDate(userId, today);

//        // 根据服务名称获取服务实例列表
//        List<ServiceInstance> instances = discoveryClient.getInstances("food-service");
//        if (CollUtil.isEmpty(instances)){
//            return Result.error("未找到food-service服务实例");
//        }
//
//        // 手写负载均衡（随机）
//        ServiceInstance instance = instances.get(RandomUtil.randomInt(instances.size()));
//
//        // 构建URL并传入请求参数
//        String url = UriComponentsBuilder
//                .fromHttpUrl(instance.getUri() + "/food/intakeOfDay")
//                .queryParam("userId", userId)
//                .queryParam("date", today.toString())
//                .toUriString();
//
//        // 调用REST接口
//        List<IntakePO> todayIntakeDetails = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<IntakePO>>() {}
//        ).getBody();

        // 远程调用food service微服务
        List<IntakePO> todayIntakeDetails = foodClient.getIntakeOfDay(today);

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

    /**
     * 计算食物重量
     * @param weightPer100g
     * @param weight
     * @return
     */
    public static int calculateWeight(Double weightPer100g, Double weight){
        return (int) Math.round(weightPer100g * weight / 100.0);
    }

    /**
     * 获取用户ById
    */
    private UserDTO getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    /**
     * 构建用户数据信息
     */
    private UserProfile buildUserProfile(UserProfileDTO dto, Long userId) {
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(dto, userProfile);
        userProfile.setUserId(userId);

        // 计算出生年份
        int currentYear = LocalDate.now().getYear();
        userProfile.setBirthYear(currentYear - dto.getAge());

        // 计算BMI
        userProfile.setBmi(userProfile.getWeight() / Math.pow(userProfile.getHeight() / 100, 2));

        // 计算BMR
        if (userProfile.getGender() == 1) {
            // 男性BMR
            userProfile.setBmr((int) Math.round(10 * userProfile.getWeight()
                    + 6.25 * userProfile.getHeight()
                    - 5 * userProfile.getAge() + 5));
        } else {
            // 女性BMR
            userProfile.setBmr((int) Math.round(10 * userProfile.getWeight()
                    + 6.25 * userProfile.getHeight()
                    - 5 * userProfile.getAge() - 161));
        }

        // 计算TDEE
        double tdee = userProfile.getBmr() * userProfile.getActivityLevel();

        // 设置每日热量目标
        if (userProfile.getGoal() == 1) {
            userProfile.setDailyCalories((int) Math.round(tdee * 1.15)); // 增肌
        } else {
            userProfile.setDailyCalories((int) Math.round(tdee * 0.85)); // 减脂
        }

        // 获取用户名
        userProfile.setUserName(userMapper.getUserNameById(userId));

        return userProfile;
    }

}

