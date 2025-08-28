package com.nt.recipe.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RecipeDTO implements Serializable {
    private Long userId;           // 用户ID
    private String name;           // 菜谱名称
    private String description;    // 菜谱描述
    private Integer cookTime;      // 烹饪时间（分钟）
    private String mealType;       // 膳食类型（breakfast, lunch, dinner, snack, dessert）

    // 食材列表
    private List<FoodIngredient> ingredients;

    // 烹饪步骤（前端传数组，存库时用换行符拼接）
    private List<String> steps;

    @Data
    public static class FoodIngredient {
        private Long foodId;       // 食物ID
        private Double weight;     // 用量（克）
    }
}
