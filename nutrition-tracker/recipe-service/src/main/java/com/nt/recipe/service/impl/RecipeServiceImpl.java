package com.nt.recipe.service.impl;

import com.nt.common.Result;
import com.nt.common.domain.vo.FoodVO;
import com.nt.recipe.client.FoodClient;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.po.RecipeFoodPO;
import com.nt.recipe.domain.po.RecipePO;
import com.nt.recipe.mapper.RecipeMapper;
import com.nt.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final FoodClient foodClient;

    @Autowired
    private RecipeMapper recipeMapper;

    @Transactional
    @Override
    public Result<String> addRecipe(RecipeDTO recipe) {

        // 参数校验
        if (recipe == null
                || recipe.getIngredients() == null
                || recipe.getIngredients().isEmpty()
                || recipe.getSteps() == null
                || recipe.getSteps().isEmpty()) {
            return Result.error("食谱信息不完整");
        }

        try {

            // 批量获取食物信息
            List<Long> foodIds = recipe.getIngredients().stream()
                    .map(RecipeDTO.FoodIngredient::getFoodId)
                    .collect(Collectors.toList());

            List<FoodVO> foods = foodClient.getFoodsByIds(foodIds).getData();
            if (foods == null || foods.size() != foodIds.size()) {
                return Result.error("获取食材信息失败");
            }

            double totalCalories = 0;
            double totalProtein = 0;
            double totalFat = 0;
            double totalCarbs = 0;

            List<RecipeFoodPO> recipeFoods = new ArrayList<>();

            // 把 List<FoodVO> 转换成 Map<Long, FoodVO>，方便按ID快速查找食材信息，而不需要每次都遍历 List
            Map<Long, FoodVO> foodMap = foods.stream()
                    .collect(Collectors.toMap(FoodVO::getId, f -> f));

            for (RecipeDTO.FoodIngredient ingredient : recipe.getIngredients()){
                FoodVO food = foodMap.get(ingredient.getFoodId());

                // 如果找不到食材信息，则跳过（防止出现空指针异常）
                if (food == null) continue;

                // 计算食谱的总热量、总蛋白质、总脂肪、总碳水化合物
                totalCalories += food.getCaloriesPer100g() * ingredient.getWeight() / 100;
                totalProtein += food.getProteinPer100g() * ingredient.getWeight() / 100;
                totalFat += food.getFatPer100g() * ingredient.getWeight() / 100;
                totalCarbs += food.getCarbsPer100g() * ingredient.getWeight() / 100;

                // 封装食谱食材信息，以便后续存入recipe_food中间表
                recipeFoods.add(new RecipeFoodPO(null, null, ingredient.getFoodId(), ingredient.getWeight()));
            }

            // 保留两位小数
            totalCalories = BigDecimal.valueOf(totalCalories).setScale(2, RoundingMode.HALF_UP).doubleValue();
            totalProtein = BigDecimal.valueOf(totalProtein).setScale(2, RoundingMode.HALF_UP).doubleValue();
            totalFat = BigDecimal.valueOf(totalFat).setScale(2, RoundingMode.HALF_UP).doubleValue();
            totalCarbs = BigDecimal.valueOf(totalCarbs).setScale(2, RoundingMode.HALF_UP).doubleValue();


            // 封装食谱信息，存入数据库的recipe表
            RecipePO recipePO = new RecipePO(
                    null,
                    recipe.getUserId(),
                    recipe.getName(),
                    recipe.getDescription(),
                    recipe.getCookTime(),
                    recipe.getMealType(),
                    String.join("\n", recipe.getSteps()), // 用换行符拼接步骤
                    totalCalories,
                    totalProtein,
                    totalFat,
                    totalCarbs
            );

            int res = recipeMapper.insertRecipe(recipePO);

            if (res <= 0) {
                return Result.error("新建食谱失败");
            }

            // 把食品信息批量插入到recipe_food中间表
            Long recipeId = recipePO.getId();
            recipeFoods.forEach(rf -> rf.setRecipeId(recipeId));
            recipeMapper.insertRecipeFoods(recipeFoods);

            log.info("新建食谱成功: userId={}, recipeId={}, name={}", recipe.getUserId(), recipeId, recipe.getName());
            return Result.success("新建食谱成功");
        } catch (Exception e) {
            log.error("添加食谱失败", e);
            return Result.error("新建食谱异常");
        }
    }
}
