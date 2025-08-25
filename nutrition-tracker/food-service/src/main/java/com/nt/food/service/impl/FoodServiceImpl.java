package com.nt.food.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nt.common.Result;
import com.nt.common.utils.RedisUtils;
import com.nt.common.utils.UserThreadLocal;
import com.nt.food.domain.dto.FavoriteDTO;
import com.nt.food.domain.dto.FoodDTO;
import com.nt.food.domain.dto.IntakeDTO;
import com.nt.food.domain.dto.MealUpdateRequestDTO;
import com.nt.food.domain.po.IntakePO;
import com.nt.food.domain.po.MealFood;
import com.nt.food.domain.vo.FavoriteVO;
import com.nt.food.domain.vo.FoodVO;
import com.nt.food.mapper.FoodMapper;
import com.nt.food.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nt.common.Constants.*;

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

    /**
     * 给食物添加标签
     * @param food
     */
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

    @Override
    public List<IntakePO> getIntakeOfDay(LocalDate date) {
        Long userId = UserThreadLocal.getUserId();
        System.out.println("获取到userId: " + userId);
        return foodMapper.getIntakesByIdAndDate(userId, date);
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

        // 删除Redis缓存
        String dateStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = REDIS_KEY_MEAL_INFO + userId + "::" + intakeInfo.getMealType() + "::" + dateStr;
        redisUtils.delete(key);
        redisUtils.deleteKeysByPrefix(REDIS_KEY_RECENT_LIST + userId);
        redisUtils.delete(REDIS_KEY_DIARY + userId + "::" + dateStr);

        return Result.success();
    }

/*    *//**
     * 获取用户当天的摄入详情
     * @param userId
     * @param date
     * @return
     *//*
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
    }*/

    /**
     *  获取食物详情
     * @param foodId
     * @return
     */
    @Override
    public Result<FoodVO> getFoodDetail(Long foodId) {
        // 尝试从Redis缓存获取数据
        FoodVO food = redisUtils.get(REDIS_KEY_FOOD_DETAIL + foodId, FoodVO.class);

        // 如果缓存中不存在，则从数据库中查询
        if  (food == null) {
            food = foodMapper.getFoodById(foodId);
        }

         // 检查食物信息是否存在
        if (food == null) {
            // 依然没有食物信息则返回错误信息

            // 缓存空对象防止穿透
            redisUtils.set(REDIS_KEY_FOOD_DETAIL + foodId, "", 5, TimeUnit.MINUTES);

            return Result.error("食物不存在");
        }
        // 把食物信息添加到缓存中
        redisUtils.set(REDIS_KEY_FOOD_DETAIL + foodId, food);

         return Result.success(food);
    }

    /**
     *  根据页码、每页容量、名称获取食物列表
     * @param page
     * @param size
     * @param name
     * @return
     */
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
            if (foods.isEmpty()) {
                redisUtils.set(key, foods, 5, TimeUnit.MINUTES); // 缓存空结果，避免缓存穿透。
            } else {
                redisUtils.set(key, foods, 30, TimeUnit.MINUTES);
            }
        }
        return foods;
    }

    /**
     * 获取用户指定餐食信息
     * @param mealType
     * @param date
     * @return
     */
    @Override
    public Result getMealInfo(int mealType, LocalDate date) {
        // 获取当前用户id
        Long userId = UserThreadLocal.getUserId();

        // 尝试从Redis缓存中获取
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = REDIS_KEY_MEAL_INFO + userId + "::" + mealType + "::" + dateStr;
        List<MealFood> foods = redisUtils.get(key, new TypeReference<List<MealFood>>() {});

        // Redis中没有数据，则从MYSQL中获取
        if (foods == null) {
            foods = foodMapper.getMealFoods(userId, mealType, date);
            // 把结果保存到Redis中
            if (foods.isEmpty()) {
                redisUtils.set(key, foods, 5, TimeUnit.MINUTES); // 缓存空结果，避免缓存穿透。
            } else {
                redisUtils.set(key, foods, 30, TimeUnit.MINUTES);
            }
        }

        // 返回结果
        return Result.success(foods);
    }

    /**
     * 修改用户指定餐食信息
     * @param request
     * @return
     */
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

        // 删除Redis缓存
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String key = REDIS_KEY_MEAL_INFO + userId + "::" + mealType + "::" + dateStr;
        redisUtils.delete(key);
        redisUtils.deleteKeysByPrefix(REDIS_KEY_RECENT_LIST + userId);
        redisUtils.delete(REDIS_KEY_DIARY + userId + "::" + dateStr);

        return Result.success();
    }

    /**
     * 收藏或取消收藏食物
     * @param favorite
     * @return
     */
    @Override
    public Result addOrRemoveFavoriteFood(FavoriteDTO favorite) {

        Long userId = UserThreadLocal.getUserId();
        Long foodId = favorite.getFoodId();

        System.out.println("userId: " + userId);

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

        // 删除Redis中的缓存
        String key = REDIS_KEY_FAVORITE_STATUS + userId + "::" + foodId;
        redisUtils.delete(key);
        String favoriteListKey = REDIS_KEY_FAVORITE_LIST + userId;
        redisUtils.deleteKeysByPrefix(favoriteListKey);

        return Result.success();
    }

    /**
     * 获取食物收藏状态
     * @param foodId
     * @return
     */
    @Override
    public Result getFavoriteStatus(Long foodId) {
        Long userId = UserThreadLocal.getUserId();

        // 尝试从Redis缓存中获取收藏状态
        String key = REDIS_KEY_FAVORITE_STATUS + userId + "::" + foodId;
        Boolean favoriteStatus = redisUtils.get(key, Boolean.class);

        // Redis中没有数据，则从MYSQL中获取
        if (favoriteStatus == null) {
            // 查询数据库中是否有该食物的收藏记录并添加到返回的VO对象中
            favoriteStatus = foodMapper.getFavoriteStatus(userId, foodId);
            // 把结果保存到Redis中
            redisUtils.set(key, favoriteStatus);
        }

        // 封装结果并返回
        FavoriteVO favoriteVO = new FavoriteVO(favoriteStatus);
        return Result.success(favoriteVO);
    }

    /**
     * 获取用户收藏食物列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public List<FoodVO> getFavoriteFoodList(Integer page, Integer size) {

        Long userId = UserThreadLocal.getUserId();

        // 尝试从Redis缓存中获取
        String key = REDIS_KEY_FAVORITE_LIST + userId + "::" + page + "::" + size;
        List<FoodVO> favoriteFoods = redisUtils.get(key, new TypeReference<List<FoodVO>>() {});

        // Redis中没有数据，则从MYSQL中获取
        if (favoriteFoods == null) {

            // 开始分页查询
            PageHelper.startPage(page, size);

            // 根据名称查询食物
            favoriteFoods = foodMapper.getFavoriteFoodsById(userId);

            // 把结果保存到Redis中
            if (favoriteFoods.isEmpty()) {
                redisUtils.set(key, favoriteFoods, CACHE_TIME_SHORT, TimeUnit.MINUTES); // 缓存空结果，避免缓存穿透。
            } else {
                redisUtils.set(key, favoriteFoods, CACHE_TIME_LONG, TimeUnit.MINUTES);
            }
        }
        return favoriteFoods;
    }

    /**
     * 获取最近食用的食物列表
     * @param limit
     * @return
     */
    @Override
    public List<FoodVO> getRecentFoodList(Integer limit) {

        Long userId = UserThreadLocal.getUserId();

        // 尝试从Redis缓存中获取
        String key = REDIS_KEY_RECENT_LIST + userId + "::" + limit;
        List<FoodVO> recentFoods = redisUtils.get(key, new TypeReference<List<FoodVO>>() {});

        // Redis中没有数据，则从MYSQL中获取
        if (recentFoods == null) {
            // 根据limit查询最近食用的食物
            recentFoods = foodMapper.getRecentFoodList(userId, limit);

            // 把结果保存到Redis中
            if (recentFoods.isEmpty()) {
                redisUtils.set(key, recentFoods, CACHE_TIME_SHORT, TimeUnit.MINUTES); // 缓存空结果，避免缓存穿透。
            } else {
                redisUtils.set(key, recentFoods, CACHE_TIME_LONG, TimeUnit.MINUTES);
            }
        }
        return recentFoods;
    }

    /**
     * 获取食物标签
     * @param foodId
     * @return
     */
    @Override
    public String getTagsByFoodId(Integer foodId) {
        // 尝试从Redis缓存中获取
        String key = REDIS_KEY_FOOD_TAG + foodId;
        List<String> tags = redisUtils.get(key, new TypeReference<List<String>>() {});

        // Redis中没有数据，则从MYSQL中获取
        if (tags == null) {
            tags = foodMapper.getTagsByFoodId(foodId);

            if (tags.isEmpty()) {
                redisUtils.set(key, tags, CACHE_TIME_SHORT, TimeUnit.MINUTES); // 缓存空结果，避免缓存穿透。
            } else {
                redisUtils.set(key, tags, CACHE_TIME_LONG, TimeUnit.MINUTES);
            }
        }

        return String.join(",", tags);  // 用逗号拼接返回
    }
}
