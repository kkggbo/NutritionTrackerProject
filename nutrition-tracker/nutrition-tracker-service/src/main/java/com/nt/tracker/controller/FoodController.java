package com.nt.tracker.controller;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
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
    public Result<String> intake(@RequestBody IntakeDTO intakeInfo) {
        return foodService.intake(intakeInfo);
    }

    @GetMapping("/{userId}/{date}")
    public Result<IntakeDetailVO> getTodayIntakeDetails(@PathVariable Long userId, @PathVariable LocalDate date) {
        return foodService.getTodayIntakeDetails(userId, date);
    }
}
