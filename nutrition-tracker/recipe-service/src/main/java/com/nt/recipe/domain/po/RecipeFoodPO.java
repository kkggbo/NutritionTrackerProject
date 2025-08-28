package com.nt.recipe.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class RecipeFoodPO implements Serializable {

    private Long id;                 // 主键ID
    private Long recipeId;           // 食谱ID
    private Long foodId;             // 食物ID
    private Double weight;           // 食物重量,克

}