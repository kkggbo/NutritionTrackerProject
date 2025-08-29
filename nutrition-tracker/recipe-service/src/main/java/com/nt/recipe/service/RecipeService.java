package com.nt.recipe.service;

import com.nt.common.Result;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.vo.RecipeVO;

public interface RecipeService {

    Result<String> addRecipe(RecipeDTO recipe);

    boolean deleteRecipe(Long id, Long userId);

    RecipeVO getRecipe(Long id);

    Result<String> updateRecipe(Long id, RecipeDTO recipeDTO);
}
