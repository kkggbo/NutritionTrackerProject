package com.nt.recipe.controller;

import com.nt.common.Result;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
@Slf4j
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    /**
     * 添加新食谱
     * @param recipe
     * @return
     */
    @PostMapping
    public Result<String> addRecipe(@RequestBody RecipeDTO recipe) {
        return recipeService.addRecipe(recipe);
    }
}
