package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.MealUpdateRequestDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;
import com.nt.tracker.service.FoodService;
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

    @PostMapping("/add")
    public Result<String> addFood(@RequestBody FoodDTO food) {
        return foodService.addFood(food);
    }

    @GetMapping("/{userId}")
    public Result<List<FoodVO>> getFoodInventory(@PathVariable Long userId) {
        return foodService.getFoodInventory(userId);
    }


    @PostMapping("/intake")
    public Result intake(@RequestBody IntakeDTO intakeInfo) {
        return foodService.intake(intakeInfo);
    }

    @GetMapping("/{userId}/{date}")
    public Result<IntakeDetailVO> getTodayIntakeDetails(@PathVariable Long userId, @PathVariable LocalDate date) {
        return foodService.getTodayIntakeDetails(userId, date);
    }

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



}
