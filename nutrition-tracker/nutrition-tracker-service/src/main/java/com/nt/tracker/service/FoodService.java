package com.nt.tracker.service;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.domain.vo.IntakeDetailVO;

import java.time.LocalDate;
import java.util.List;

public interface FoodService {

    Result<String> addFood(FoodDTO food);

    Result<List<FoodVO>> getFoodInventory(Long userId);

    Result<String> intake(IntakeDTO intakeInfo);

    Result<IntakeDetailVO> getTodayIntakeDetails(Long userId, LocalDate date);
}
