package com.nt.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryVO {
    private int totalCalories;         // 总摄入热量（单位：kcal）
    private int goalCalories;          // 热量目标（单位：kcal）
    private List<Macro> macros;        // 三大营养素列表
    private List<Meal> meals;          // 每餐摄入情况

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Macro {
        private int label;             // 1=碳水, 2=蛋白质, 3=脂肪
        private int value;             // 当前摄入值（g）
        private int goal;              // 目标摄入值（g）
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meal {
        private int name;              // 1=早餐, 2=中餐, 3=晚餐, 4=加餐
        private int calories;          // 每餐热量（kcal）
    }
}