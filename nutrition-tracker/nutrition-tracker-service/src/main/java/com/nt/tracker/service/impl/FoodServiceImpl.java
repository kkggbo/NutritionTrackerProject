package com.nt.tracker.service.impl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.*;
import com.nt.tracker.domain.po.MealFood;
import com.nt.tracker.domain.vo.FavoriteVO;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.mapper.AuthMapper;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.mapper.UserMapper;
import com.nt.tracker.service.AuthService;
import com.nt.tracker.service.FoodService;
import com.nt.tracker.utils.RedisUtils;
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
import java.util.concurrent.TimeUnit;

import static com.nt.tracker.common.Constants.*;

@Service
@Slf4j
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private RedisUtils redisUtils;

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

        // 获取用户id
        Long userId = UserThreadLocal.getUserId();
        food.setUserId(userId);

        foodMapper.addFood(food);

        // 为食物添加标签
        addTags(food);

        // 删除Redis缓存
        redisUtils.deleteKeysByPrefix(REDIS_KEY_FOOD_LIST);

        return Result.success();
    }

    public void addTags(FoodDTO food) {
        // 获取食物营养含量
        Double calories = food.getCaloriesPer100g();
        Double carbs = food.getCarbsPer100g();
        Double protein = food.getProteinPer100g();
        Double fat = food.getFatPer100g();

        List<Integer> tagIds = new ArrayList<>();

        if (protein != null && protein >= 10) {
            tagIds.add(FOOD_TAG_HIGH_PROTEIN);
        }
        if (fat != null && fat <= 3) {
            tagIds.add(FOOD_TAG_LOW_FAT);
        }
        if (fat != null && fat >= 17) {
            tagIds.add(FOOD_TAG_HIGH_FAT);
        }
        if (carbs != null && carbs <= 5) {
            tagIds.add(FOOD_TAG_LOW_CARBOHYDRATE);
        }
        if (carbs != null && carbs >= 30) {
            tagIds.add(FOOD_TAG_HIGH_CARBOHYDRATE);
        }
        if (calories != null && calories >= 300) {
            tagIds.add(FOOD_TAG_HIGH_CALORIE);
        }
        if (calories != null && calories <= 40) {
            tagIds.add(FOOD_TAG_LOW_CALORIE);
        }
        if (protein != null && protein >= 15 && fat != null && fat <= 10) {
            tagIds.add(FOOD_TAG_BUILD_MUSCLE);
        }
        if (calories != null && calories <= 100 && fat != null && fat <= 3) {
            tagIds.add(FOOD_TAG_LOSE_WEIGHT);
        }

        // 添加标签
        foodMapper.addTags(tagIds, food.getId());

    }
    /**
     * 获取食物库存列表（优先从 Redis 缓存中获取）
     * @param userId 用户ID
     * @return 带有用户食物库存的结果包装类
     */
    @Override
    public Result<List<FoodVO>> getFoodInventory(Long userId) {
        // 在Redis中查询，如果存在则直接返回
        List<FoodVO> foodInventory = redisUtils.get(REDIS_KEY_USER_FOOD_INVENTORY + userId, new TypeReference<List<FoodVO>>() {});

        // Redis中不存在，查询mysql
        if (foodInventory == null) {
            foodInventory= foodMapper.getFoodInventory(userId);
            // 将查询结果存入Redis
            redisUtils.set(REDIS_KEY_USER_FOOD_INVENTORY + userId, foodInventory, 30, TimeUnit.MINUTES);
        }

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

        // 尝试从Redis缓存中获取
        String key = REDIS_KEY_FOOD_LIST + name + "::" + page + "::" + size;
        List<FoodVO> foods = redisUtils.get(key, new TypeReference<List<FoodVO>>() {});

        // Redis中没有数据，则从MYSQL中获取
        if (foods == null) {
            // 开始分页查询
            PageHelper.startPage(page, size);

            // 根据名称查询食物
            Page<FoodVO> pageFoods = foodMapper.foodQuery(name);
            foods = pageFoods.getResult();

            // 将结果保存到Redis中
            redisUtils.set(key, foods, 30, TimeUnit.MINUTES);
        }
        return foods;
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

    @Override
    public Result addOrRemoveFavoriteFood(FavoriteDTO favorite) {

        Long userId = UserThreadLocal.getUserId();
        Long foodId = favorite.getFoodId();

        // 判断是收藏还是取消收藏
        if (favorite.getFavorite()) {
            try {
                // 直接尝试插入新数据
                foodMapper.addFavorite(userId, foodId);
            } catch (DuplicateKeyException e) {
                // 出现重复数据，说明已收藏，忽略异常
            }
        } else {
            // 取消收藏
            foodMapper.removeFavorite(userId, foodId);
        }
        return Result.success();
    }

    @Override
    public Result getFavoriteStatus(Long foodId) {
        Long userId = UserThreadLocal.getUserId();

        // 查询数据库中是否有该食物的收藏记录并添加到返回的VO对象中
        FavoriteVO favoriteVO = new FavoriteVO(foodMapper.getFavoriteStatus(userId, foodId));

        return Result.success(favoriteVO);
    }

    @Override
    public List<FoodVO> getFavoriteFoodList(Integer page, Integer size) {

        Long userId = UserThreadLocal.getUserId();

        // 开始分页查询
        PageHelper.startPage(page, size);

        // 根据名称查询食物
        return foodMapper.getFavoriteFoodsById(userId);
    }

    @Override
    public List<FoodVO> getRecentFoodList(Integer limit) {

        Long userId = UserThreadLocal.getUserId();
        // 根据limit查询最近食用的食物
        return foodMapper.getRecentFoodList(userId, limit);
    }

    @Override
    public Object getTagsByFoodId(Integer foodId) {
        List<String> tags = foodMapper.getTagsByFoodId(foodId);
        return String.join(",", tags);  // 用逗号拼接返回
    }
}
