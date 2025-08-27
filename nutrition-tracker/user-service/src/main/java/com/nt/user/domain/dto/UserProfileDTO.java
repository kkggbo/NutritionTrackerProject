package com.nt.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileDTO implements Serializable {

    // 用户名
    private String username;

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
}
