package com.nt.recipe.controller;

import com.nt.common.Result;
import com.nt.common.utils.UserThreadLocal;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.vo.CommentVO;
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

    /**
     * 点赞食谱
     * @param recipeId
     * @return
     */
    @PostMapping("/like/{recipeId}")
    public Result<String> likeRecipe(@PathVariable Long recipeId) {
        boolean success = recipeService.likeRecipe(recipeId);
        return success ? Result.success("点赞成功") : Result.error("已点赞过");
    }

    /**
     * 取消点赞
     * @param recipeId
     * @return
     */
    @DeleteMapping("/like/{recipeId}")
    public Result<?> unlikeRecipe(@PathVariable Long recipeId) {
        boolean success = recipeService.unlikeRecipe(recipeId);
        return success ? Result.success("取消点赞成功") : Result.error("未点赞过");
    }

    /**
     * 获取点赞数
     * @param recipeId
     * @return
     */
    @GetMapping("/like/count/{recipeId}")
    public Result<Integer> getLikeCount(@PathVariable Long recipeId) {
        return Result.success(recipeService.getLikeCount(recipeId));
    }

    /**
     * 获取点赞状态
     * @param recipeId
     * @return
     */
    @GetMapping("/like/status/{recipeId}")
    public Result<Boolean> isLiked(@PathVariable Long recipeId) {
        return Result.success(recipeService.isLiked(recipeId));
    }

    /**
     * 收藏食谱
     *
     * @param recipeId 食谱ID
     * @return 操作结果
     */
    @PostMapping("/favorite/{recipeId}")
    public Result<String> favoriteRecipe(@PathVariable Long recipeId) {
        boolean success = recipeService.favoriteRecipe(recipeId);
        return success ? Result.success("收藏成功") : Result.error("已收藏过");
    }

    /**
     * 取消收藏食谱
     *
     * @param recipeId 食谱ID
     * @return 操作结果
     */
    @DeleteMapping("/favorite/{recipeId}")
    public Result<String> unfavoriteRecipe(@PathVariable Long recipeId) {
        boolean success = recipeService.unfavoriteRecipe(recipeId);
        return success ? Result.success("取消收藏成功") : Result.error("未收藏过");
    }

    /**
     * 获取食谱收藏数
     *
     * @param recipeId 食谱ID
     * @return 收藏总数
     */
    @GetMapping("/favorite/count/{recipeId}")
    public Result<Integer> getFavoriteCount(@PathVariable Long recipeId) {
        return Result.success(recipeService.getFavoriteCount(recipeId));
    }

    /**
     * 检查当前用户是否收藏该食谱
     *
     * @param recipeId 食谱ID
     * @return true 表示已收藏，false 表示未收藏
     */
    @GetMapping("/favorite/status/{recipeId}")
    public Result<Boolean> isFavorited(@PathVariable Long recipeId) {
        return Result.success(recipeService.isFavorited(recipeId));
    }

    /**
     * 为当前食谱新增评论
     * @param recipeId 食谱ID
     * @param commentContent 评论内容
     * @return Result<String>
     */
    @PostMapping("/comment/{recipeId}")
    public Result<String> addComment(@PathVariable Long recipeId,
                                     @RequestParam String commentContent) {
        boolean success = recipeService.addComment(recipeId, commentContent);
        return success ? Result.success("评论成功") : Result.error("评论失败");
    }

    /**
     * 获取当前食谱的所有评论
     * @param recipeId 食谱ID
     * @return Result<List<CommentVO>>
     */
    @GetMapping("/comment/{recipeId}")
    public Result<List<CommentVO>> getComments(@PathVariable Long recipeId) {
        List<CommentVO> comments = recipeService.getComments(recipeId);
        return Result.success(comments);
    }

}
