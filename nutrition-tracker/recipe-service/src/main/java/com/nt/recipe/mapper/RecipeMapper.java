package com.nt.recipe.mapper;


import com.nt.recipe.domain.po.RecipeFoodPO;
import com.nt.recipe.domain.po.RecipePO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RecipeMapper {
    int insertRecipe(RecipePO recipe);

    void insertRecipeFoods(List<RecipeFoodPO> foods);

    @Select("SELECT id, recipe_id, food_id, weight FROM recipe_food WHERE recipe_id = #{recipeId}")
    List<RecipeFoodPO> getRecipeFoods(Long recipeId);

    RecipePO getRecipeById(Long id);

    @Delete("DELETE FROM recipe WHERE id = #{id}")
    int deleteRecipe(Long id);

    @Delete("DELETE FROM recipe_food WHERE recipe_id = #{id}")
    int deleteRecipeFoods(Long id);

    int updateRecipe(RecipePO updatedRecipe);

    @Delete("DELETE FROM recipe_food WHERE recipe_id = #{recipeId}")
    void deleteRecipeFoodsByRecipeId(Long recipeId);
}
