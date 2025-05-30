package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfile implements Serializable {
    //  用户id
    private Long userId;

    // 性别（1=男，2=女）
    private Integer gender;

    // 年龄
    private Integer age;

    // 身高
    private Double height;

    // 体重
    private Double weight;

    // 目标（1=增肌，2=减脂）
    private Integer goal;

    // 活动等级
    private Double activityLevel;

    // BMI
    private Double bmi;

    // BMR
    private Integer bmr;

    // 每日热量目标
    private Integer dailyCalories;
}
