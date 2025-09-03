package com.nt.recipe.mapper;


import com.github.pagehelper.Page;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.po.CommentPO;
import com.nt.recipe.domain.po.RecipeFoodPO;
import com.nt.recipe.domain.po.RecipePO;
import com.nt.recipe.domain.vo.CommentVO;
import com.nt.recipe.domain.vo.RecipeListVO;
import org.apache.ibatis.annotations.*;

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

    Page<RecipeListVO> recipeQuery(RecipeQueryDTO dto);

    // 检查是否点赞过
    int existsLike(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 插入点赞
    int insertLike(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 删除点赞
    int deleteLike(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 点赞数 +1
    int increaseLikeCount(@Param("recipeId") Long recipeId);

    // 点赞数 -1
    int decreaseLikeCount(@Param("recipeId") Long recipeId);

    // 获取点赞数
    int getLikeCount(@Param("recipeId") Long recipeId);

    // 检查用户是否收藏
    Integer checkFavorite(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 添加收藏
    int insertFavorite(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 删除收藏
    int deleteFavorite(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    // 收藏数 +1
    int incrementFavoriteCount(@Param("recipeId") Long recipeId);

    // 收藏数 -1
    int decrementFavoriteCount(@Param("recipeId") Long recipeId);

    // 获取收藏数
    Integer getFavoriteCount(@Param("recipeId") Long recipeId);

    int insertComment(CommentPO comment);

    List<CommentVO> selectCommentsByRecipeId(Long recipeId);
}
