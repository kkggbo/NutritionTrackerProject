package com.nt.tracker.domain.vo;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class IntakeDetailVO implements Serializable {
    private LocalDate date;
    private List<FoodVO> foodList;
    private OverallInfo overallInfo;

    // OverallInfo内部类
    @Data
    private static class OverallInfo {
        private Double totalCalories;
        private Double totalCarbs;
        private Double totalProtein;
        private Double totalFat;
    }

    public void addFood(FoodVO food) {
        if (foodList == null) {
            foodList = new ArrayList<>();
        }
        foodList.add(food);
    }

    public void setOverallInfo(Double totalCalories, Double totalCarbs, Double totalProtein, Double totalFat) {
        this.overallInfo = new OverallInfo();
        this.overallInfo.setTotalCalories(totalCalories);
        this.overallInfo.setTotalCarbs(totalCarbs);
        this.overallInfo.setTotalProtein(totalProtein);
        this.overallInfo.setTotalFat(totalFat);
    }
}