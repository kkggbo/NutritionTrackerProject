package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.*;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;

import java.time.LocalDate;
import java.util.List;

public interface FoodService {

    Result<String> addFood(FoodDTO food);

    Result<List<FoodVO>> getFoodInventory(Long userId);


    public Result<String> intake(IntakeDTO intakeInfo);

    public Result<IntakeDetailVO> getTodayIntakeDetails(Long userId, LocalDate date);

    Result<FoodVO> getFoodDetail(Long foodId);

    List<FoodVO> getFoods(int page, int size, String name);

    Result getMealInfo(int mealType, LocalDate date);

    Result updateMealFoods(MealUpdateRequestDTO request);

    Result addOrRemoveFavoriteFood(FavoriteDTO favorite);

    Result getFavoriteStatus(Long foodId);

    List<FoodVO> getFavoriteFoodList(Integer page, Integer size);

    List<FoodVO> getRecentFoodList(Integer limit);

    String getTagsByFoodId(Integer foodId);

    public void addTags(FoodDTO food);
}
