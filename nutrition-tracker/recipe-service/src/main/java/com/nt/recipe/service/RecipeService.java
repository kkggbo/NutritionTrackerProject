package com.nt.recipe.service;

import com.nt.common.Result;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.vo.RecipeListVO;
import com.nt.recipe.domain.vo.RecipeVO;

import java.util.List;

public interface RecipeService {

    Result<String> addRecipe(RecipeDTO recipe);

    boolean deleteRecipe(Long id, Long userId);

    RecipeVO getRecipe(Long id);

    Result<String> updateRecipe(Long id, RecipeDTO recipeDTO);

    List<RecipeListVO> queryRecipes(RecipeQueryDTO dto);
}
