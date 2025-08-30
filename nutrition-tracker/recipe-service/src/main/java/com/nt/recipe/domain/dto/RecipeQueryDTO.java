package com.nt.recipe.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecipeQueryDTO implements Serializable {
    private String name;             // 食谱名称（模糊查询）
    private Integer cookTimeMin;     // 最小烹饪时间
    private Integer cookTimeMax;     // 最大烹饪时间
    private Double caloriesMin;      // 最小热量
    private Double caloriesMax;      // 最大热量
    private String mealType;         // 膳食类型（breakfast, lunch, dinner, snack, dessert）
    private List<Long> foodIds;      // 食材ID集合（允许多个）
    private Integer foodCount = 0;       // 食材数量
    private Integer pageNo = 1;      // 页码
    private Integer pageSize = 10;   // 每页数量
}
