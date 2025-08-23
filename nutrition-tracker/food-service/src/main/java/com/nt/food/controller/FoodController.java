package com.nt.food.controller;

import com.nt.common.Result;
import com.nt.food.domain.dto.FavoriteDTO;
import com.nt.food.domain.dto.FoodDTO;
import com.nt.food.domain.dto.IntakeDTO;
import com.nt.food.domain.dto.MealUpdateRequestDTO;
import com.nt.food.domain.po.IntakePO;
import com.nt.food.domain.vo.FoodVO;
import com.nt.food.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/food")
@Slf4j
public class FoodController {
    @Autowired
    private FoodService foodService;

    /**
     *  添加食物：添加用户自定义的食物及其营养信息，便于后续记录饮食摄入。
     * @param food
     * @return
     */
    @PostMapping("/add")
    public Result<String> addFood(@RequestBody FoodDTO food) {
        return foodService.addFood(food);
    }


    /*    @GetMapping("/{userId}")
    public Result<List<FoodVO>> getFoodInventory(@PathVariable Long userId) {
        return foodService.getFoodInventory(userId);
    }*/


    @PostMapping("/intake")
    public Result intake(@RequestBody IntakeDTO intakeInfo) {
        return foodService.intake(intakeInfo);
    }

/*    *//**
     *  添加用户进食某种食物信息
     * @param userId
     * @param date
     * @return
     *//*
    @GetMapping("/{userId}/{date}")
    public Result<IntakeDetailVO> getTodayIntakeDetails(@PathVariable Long userId, @PathVariable LocalDate date) {
        return foodService.getTodayIntakeDetails(userId, date);
    }*/

    /**
     *  根据食物id获取某种食物详情
     * @param foodId
     * @return
     */
    @GetMapping ("/detail/{foodId}")
    public Result<FoodVO> getFoodDetail(@PathVariable Long foodId) {
        return foodService.getFoodDetail(foodId);
    }

    /**
     *  获取食物列表（分页 + 模糊查询）
     * @param page
     * @param size
     * @param name
     * @return
     */
    @GetMapping("/list")
    public Result listFoods(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "") String name
    ) {
        List<FoodVO> foods = foodService.getFoods(page, size, name);
        return Result.success(foods);
    }

    /**
     *  获取某天某餐食物列表
     * @param mealType
     * @param date
     * @return
     */
    @GetMapping("/meal")
    public Result getMealInfo(@RequestParam int mealType, @RequestParam LocalDate date) {
        return foodService.getMealInfo(mealType, date);
    }

    /**
     *  更新某天某餐食物列表
     * @param request
     * @return
     */
    @PostMapping("/meal/update")
    public Result updateMealFoods(@RequestBody MealUpdateRequestDTO request) {
        return foodService.updateMealFoods(request);
    }

    /**
     *  添加或删除收藏食物
     * @param favorite
     * @return
     */
    @PostMapping("/favorite")
    public Result addOrRemoveFavoriteFood(@RequestBody FavoriteDTO favorite) {
        return foodService.addOrRemoveFavoriteFood(favorite);
    }

    /**
     *  获取收藏食物状态
     * @param foodId
     * @return
     */
    @GetMapping("/favorite/status/{foodId}")
    public Result getFavoriteStatus(@PathVariable Long foodId) {
        return foodService.getFavoriteStatus(foodId);
    }

    /**
     *  获取收藏食物列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/favorite/list")
    public Result getFavoriteFoodList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size){
        List<FoodVO> foods = foodService.getFavoriteFoodList(page, size);
        return Result.success(foods);
    }

    /**
     *  获取最近食物列表
     * @param limit
     * @return
     */
    @GetMapping("/recent/list")
    public Result getRecentFoodList(
            @RequestParam(defaultValue = "20") Integer limit) {

        List<FoodVO> foods = foodService.getRecentFoodList(limit);
        return Result.success(foods);
    }

    /**
     *  根据食物ID获取该食物对应的标签列表，标签以字符串形式返回，标签之间用英文逗号 , 隔开。
     * @param foodId
     * @return
     */
    @GetMapping("/tag/list")
    public Result getTagList(@RequestParam Integer foodId) {
        return Result.success(foodService.getTagsByFoodId(foodId));
    }

    /**
     *  根据用户id和日期获取当日摄入情况
     * @param userId
     * @param date
     * @return
     */

    @GetMapping("/intakeOfDay")
    public List<IntakePO> getIntakeOfDay(@RequestParam Long userId, @RequestParam LocalDate date) {
        return foodService.getIntakeOfDay(userId, date);
    }

}
