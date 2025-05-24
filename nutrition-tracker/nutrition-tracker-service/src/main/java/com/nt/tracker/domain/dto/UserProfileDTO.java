package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileDTO implements Serializable {
    //  用户id
    private Long userId;

    // 性别
    private Integer gender;

    // 年龄
    private Integer age;

    // 身高
    private Double height;

    // 体重
    private Double weight;
}
