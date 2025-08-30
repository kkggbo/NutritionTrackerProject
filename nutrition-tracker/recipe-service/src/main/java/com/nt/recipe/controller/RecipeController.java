package com.nt.recipe.controller;

import com.nt.common.Result;
import com.nt.common.utils.UserThreadLocal;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.vo.RecipeListVO;
import com.nt.recipe.domain.vo.RecipeVO;
import com.nt.recipe.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 删除食谱
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteRecipe(@PathVariable Long id) {
        Long userId = UserThreadLocal.getUserId();
        boolean success = recipeService.deleteRecipe(id, userId);
        return success ? Result.success("删除食谱成功") : Result.error("删除食谱失败，可能无权限或不存在");
    }

    /**
     * 查询食谱
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<RecipeVO> getRecipe(@PathVariable Long id) {
        RecipeVO recipe = recipeService.getRecipe(id);
        if (recipe == null) {
            return Result.error("查询失败，食谱不存在");
        }
        return Result.success(recipe);
    }

    /**
     * 更新食谱
     * @param id
     * @param recipeDTO
     * @return
     */
    @PutMapping("/{id}")
    public Result<String> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        return recipeService.updateRecipe(id, recipeDTO);
    }

    @PostMapping("/query")
    public Result<List<RecipeListVO>> queryRecipes(@RequestBody RecipeQueryDTO dto) {
        return Result.success(recipeService.queryRecipes(dto));
    }


}
