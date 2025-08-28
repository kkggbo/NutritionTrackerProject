package com.nt.recipe.service;

import com.nt.common.Result;
import com.nt.recipe.domain.dto.RecipeDTO;

public interface RecipeService {

    Result<String> addRecipe(RecipeDTO recipe);
}
