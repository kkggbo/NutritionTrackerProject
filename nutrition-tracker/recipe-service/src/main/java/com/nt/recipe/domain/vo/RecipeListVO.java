package com.nt.recipe.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecipeListVO implements Serializable {
    private Long id;                // 菜谱ID
    private String name;            // 菜谱名称
    private String description;     // 菜谱描述
    private Integer cookTime;       // 烹饪时间
    private String mealType;        // 膳食类型（breakfast, lunch, dinner 等）
    private Double totalCalories;   // 总卡路里
}
