package com.nt.recipe.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class RecipePO implements Serializable {
    private Long id;                // 主键ID
    private Long userId;            // 用户ID
    private String name;            // 菜谱名称
    private String description;     // 菜谱描述
    private Integer cookTime;       // 烹饪时间（分钟）
    private String mealType;        // 膳食类型（breakfast, lunch, dinner, snack, dessert）
    private String steps;           // 烹饪步骤（前端传数组，存库时用换行符拼接）
    private Double totalCalories;   // 总卡路里
    private Double totalProtein;    // 总蛋白质
    private Double totalFat;        // 总脂肪
    private Double totalCarbs;      // 总碳水
}
