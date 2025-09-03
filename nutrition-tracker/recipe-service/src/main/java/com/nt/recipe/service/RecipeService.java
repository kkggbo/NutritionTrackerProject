package com.nt.recipe.service;

import com.nt.common.Result;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.vo.CommentVO;
import com.nt.recipe.domain.vo.RecipeListVO;
import com.nt.recipe.domain.vo.RecipeVO;

import java.util.List;

public interface RecipeService {

    Result<String> addRecipe(RecipeDTO recipe);

    boolean deleteRecipe(Long id, Long userId);

    RecipeVO getRecipe(Long id);

    Result<String> updateRecipe(Long id, RecipeDTO recipeDTO);

    List<RecipeListVO> queryRecipes(RecipeQueryDTO dto);

    boolean likeRecipe(Long recipeId);

    boolean unlikeRecipe(Long recipeId);

    int getLikeCount(Long recipeId);

    boolean isLiked(Long recipeId);

    Boolean isFavorited(Long recipeId);

    Integer getFavoriteCount(Long recipeId);

    boolean unfavoriteRecipe(Long recipeId);

    boolean favoriteRecipe(Long recipeId);

    /**
     * 为指定食谱新增评论
     * @param recipeId 食谱ID
     * @param content 评论内容
     * @return true表示成功，false表示失败
     */
    boolean addComment(Long recipeId, String content);

    /**
     * 获取指定食谱的所有评论
     * @param recipeId 食谱ID
     * @return 评论列表
     */
    List<CommentVO> getComments(Long recipeId);
}
