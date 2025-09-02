package com.nt.recipe.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import com.nt.common.Result;
import com.nt.common.domain.vo.FoodVO;
import com.nt.common.utils.RedisUtils;
import com.nt.recipe.client.FoodClient;
import com.nt.recipe.domain.dto.RecipeDTO;
import com.nt.recipe.domain.dto.RecipeQueryDTO;
import com.nt.recipe.domain.po.RecipeFoodPO;
import com.nt.recipe.domain.po.RecipePO;
import com.nt.recipe.domain.vo.RecipeListVO;
import com.nt.recipe.domain.vo.RecipeVO;
import com.nt.recipe.mapper.RecipeMapper;
import com.nt.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nt.common.Constants.REDIS_KEY_RECIPE_DETAIL;
import static com.nt.common.Constants.REDIS_KEY_RECIPE_FOODS;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final FoodClient foodClient;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 新增食谱
     *
     * @param recipe
     * @return
     */
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
            List<RecipeFoodPO> recipeFoods = new ArrayList<>();

            RecipePO recipePO = operateFood(foods, recipe, recipeFoods);

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

    /**
     * 删除食谱
     *
     * @param id
     * @param userId
     * @return
     */
    @Override
    @Transactional  // 删除涉及多表，开启事务保证一致性
    public boolean deleteRecipe(Long id, Long userId) {
        // 检查食谱创建者是否是当前用户
        RecipePO recipe = recipeMapper.getRecipeById(id);
        if(recipe == null || !recipe.getUserId().equals(userId)){
            return false;
        }

        // 删除关联的食材表
        recipeMapper.deleteRecipeFoods(id);

        // 删除食谱
        int rows = recipeMapper.deleteRecipe(id);

        // 删除redis缓存
        redisUtils.delete(REDIS_KEY_RECIPE_DETAIL + id);
        log.info("删除Redis缓存成功: {}", REDIS_KEY_RECIPE_DETAIL + id);

        return rows > 0;
    }

    /**
     * 获取食谱
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public RecipeVO getRecipe(Long id) {

        // 尝试从redis中获取vo
        String cached = redisUtils.getRaw(REDIS_KEY_RECIPE_DETAIL + id);

        // 判断缓存中是否为空值（食谱不存在）
        if ("NULL".equals(cached)) {
            return null;
        }
        // 若缓存中不为null也不为"NULL"，则返回缓存中的数据
        RecipeVO RedisVO = redisUtils.get(REDIS_KEY_RECIPE_DETAIL + id, RecipeVO.class);
        if (cached != null) {
            log.info("从redis中获取食谱成功: {}", cached);
            return RedisVO;
        }

        // 缓存中的值为null，查询数据库
        RecipePO po = recipeMapper.getRecipeById(id);

        // 判断食谱是否存在
        if (po == null) {
            // 缓存空对象5分钟，防止缓存穿透
            redisUtils.set(REDIS_KEY_RECIPE_DETAIL + id, "NULL", 5, TimeUnit.MINUTES);
            return null;
        }

        // 组装VO
        RecipeVO vo = new RecipeVO(
                po.getId(),
                po.getName(),
                po.getUserId(),
                po.getDescription(),
                po.getCookTime(),
                po.getMealType(),
                null, // 稍后获取食材信息
                po.getSteps() != null ? Arrays.asList(po.getSteps().split("\n")) : new ArrayList<>(), // 拆解步骤
                po.getTotalCalories(),
                po.getTotalProtein(),
                po.getTotalFat(),
                po.getTotalCarbs()
        );

        // 获取食材信息
        // 查询recipe_food表获取食材id和重量
        List<RecipeFoodPO> recipeFoods = recipeMapper.getRecipeFoods(id);

        // 获取食材ids
        List<Long> foodIds = recipeFoods.stream()
                .map(RecipeFoodPO::getFoodId)
                .collect(Collectors.toList());

        // 远程调用food-service查询食材名字
        List<FoodVO> foods = foodClient.getFoodsByIds(foodIds).getData();

        // 根据recipeFoods和foods组装食材信息
        // 先把foods转成Map，key是foodId，value是FoodVO
        Map<Long, FoodVO> foodMap = foods.stream()
                .collect(Collectors.toMap(FoodVO::getId, Function.identity()));

        // 根据recipeFoods组装FoodIngredient列表
        List<RecipeVO.FoodIngredient> ingredients = recipeFoods.stream()
                .map(rf -> {
                    FoodVO food = foodMap.get(rf.getFoodId());
                    if (food != null) {
                        RecipeVO.FoodIngredient fi = new RecipeVO.FoodIngredient();
                        fi.setFoodId(food.getId());
                        fi.setName(food.getName());
                        fi.setWeight(rf.getWeight());
                        return fi;
                    } else {
                        return null; //找不到对应food，返回null
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        vo.setIngredients(ingredients);

        // 把结果储存到redis
        redisUtils.set(REDIS_KEY_RECIPE_DETAIL + id, vo, 30, TimeUnit.DAYS);
        log.info("添加Redis缓存成功: {}", REDIS_KEY_RECIPE_DETAIL + id);
        return vo;
    }

    @Override
    @Transactional
    public Result<String> updateRecipe(Long recipeId, RecipeDTO recipe) {
        // 参数校验
        if (recipeId == null || recipe == null
                || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()
                || recipe.getSteps() == null || recipe.getSteps().isEmpty()) {
            return Result.error("食谱信息不完整");
        }

        // 查询食谱是否存在
        RecipePO existing = recipeMapper.getRecipeById(recipeId);
        if (existing == null) {
            return Result.error("食谱不存在");
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

            List<RecipeFoodPO> recipeFoods = new ArrayList<>();

            // 计算总营养并组装RecipePO
            RecipePO updatedRecipe = operateFood(foods, recipe, recipeFoods);
            updatedRecipe.setId(recipeId); // 设置要更新的ID

            // 更新recipe表
            int res = recipeMapper.updateRecipe(updatedRecipe);
            if (res <= 0) {
                return Result.error("修改食谱失败");
            }

            // 删除原有中间表记录
            recipeMapper.deleteRecipeFoodsByRecipeId(recipeId);

            // 批量插入新的食材信息
            recipeFoods.forEach(rf -> rf.setRecipeId(recipeId));
            recipeMapper.insertRecipeFoods(recipeFoods);

            // 删除Redis缓存
            redisUtils.delete(REDIS_KEY_RECIPE_DETAIL + recipeId);
            log.info("删除Redis缓存成功: {}", REDIS_KEY_RECIPE_DETAIL + recipeId);

            log.info("修改食谱成功: userId={}, recipeId={}, name={}", recipe.getUserId(), recipeId, recipe.getName());
            return Result.success("修改食谱成功");

        } catch (Exception e) {
            log.error("修改食谱失败", e);
            return Result.error("修改食谱异常");
        }
    }

    @Override
    public List<RecipeListVO> queryRecipes(RecipeQueryDTO dto) {

        // 开始分页查询，设置页码和每页大小
        PageHelper.startPage(dto.getPageNo(), dto.getPageSize());

        // 查询食谱列表
        // 记录食物数量，便于查询
        if (dto.getFoodIds() != null) {
            dto.setFoodCount(dto.getFoodIds().size());
        } else {
            dto.setFoodCount(0);
        }
        Page<RecipeListVO> pageRecipes = recipeMapper.recipeQuery(dto);

        log.info("分页查询成功: " + pageRecipes.getResult());

        return pageRecipes.getResult();
    }

    // 根据食物列表返回处理后的食谱持久化PO对象
    public RecipePO operateFood(List<FoodVO> foods, RecipeDTO recipe, List<RecipeFoodPO> recipeFoods){
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

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
        return new RecipePO(
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
    }
}
