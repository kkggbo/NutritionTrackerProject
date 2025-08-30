package com.nt.recipe.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeVO implements Serializable {
    private Long id;                            // 菜谱ID
    private String name;                        // 菜谱名称
    private Long userId;                        // 用户ID
    private String description;                 // 菜谱描述
    private Integer cookTime;                   // 烹饪时间（分钟）
    private String mealType;                    // 膳食类型（breakfast, lunch, dinner, snack, dessert）
    private List<FoodIngredient> ingredients;   // 食材列表（包含食物名称、id和重量）
    private List<String> steps;                 // 烹饪步骤（将数据库中的字符串根据换行符分割为数组）
    private Double totalCalories;               // 总卡路里
    private Double totalProtein;                // 总蛋白质
    private Double totalFat;                    // 总脂肪
    private Double totalCarbs;                  // 总碳水

    @Data
    public static class FoodIngredient {
        private Long foodId;       // 食物ID
        private String name;       // 食物名称
        private Double weight;     // 用量（克）
    }
}
