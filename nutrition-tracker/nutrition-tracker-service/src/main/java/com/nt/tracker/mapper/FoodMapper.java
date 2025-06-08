package com.nt.tracker.mapper;

import com.github.pagehelper.Page;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.po.IntakePO;
import com.nt.tracker.domain.po.MealFood;
import com.nt.tracker.domain.vo.FoodVO;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FoodMapper {


    @Select("select * from food where name = #{name}")
    FoodVO getFoodByName(String name);

    @Insert("insert into food (user_id, name, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g) values (#{userId}, #{name}, #{caloriesPer100g}, #{carbsPer100g}, #{proteinPer100g}, #{fatPer100g})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addFood(FoodDTO food);

    @Select("select * from food where user_id = #{userId}")
    List<FoodVO> getFoodInventory(Long userId);

    void addIntake(IntakeDTO intakeInfo, Long userId);

    @Select("select * from food where id = #{foodId}")
    FoodVO getFoodById(Long foodId);


    List<IntakePO> getIntakesByIdAndDate(Long userId, LocalDate date);

    /**
     * 根据名称查询食物
     * @param name
     * @return
     */
    Page<FoodVO> foodQuery(String name);

    List<MealFood> getMealFoods(Long userId, int mealType, LocalDate date);

    @Update("update food_intake set weight = #{food.weight} where user_id = #{userId} and meal_type = #{mealType} and intake_date = #{date} and food_id = #{food.id}")
    void updateMealFoods(Long userId, int mealType, LocalDate date, MealFood food);

    void deleteMealFoods(Long userId, int mealType, LocalDate date, List<Long> deletedFoodIds);

    @Select("select * from food_intake where user_id = #{userId} and meal_type = #{mealType} and intake_date = #{date} and food_id = #{foodId}")
    IntakeDTO getIntakeFood(Long userId, Integer mealType, LocalDate date, Long foodId);

    @Update("update food_intake set weight = #{weight} where user_id = #{userId} and meal_type = #{mealType} and intake_date = #{date} and food_id = #{foodId}")
    void updateMealFoodWeight(Long userId, Integer mealType, LocalDate date, Long foodId, Double weight);

    @Insert("INSERT IGNORE INTO food_favorite (user_id, food_id, created_time) VALUES (#{userId}, #{foodId}, NOW())")
    void addFavorite(Long userId, Long foodId);

    @Delete("DELETE FROM food_favorite WHERE user_id = #{userId} AND food_id = #{foodId}")
    void removeFavorite(Long userId, Long foodId);

    @Select("SELECT EXISTS (SELECT 1 FROM food_favorite WHERE user_id = #{userId} AND food_id = #{foodId})")
    boolean getFavoriteStatus(Long userId, Long foodId);

    List<FoodVO> getFavoriteFoodsById(Long userId);

    List<FoodVO> getRecentFoodList(Long userId, Integer limit);

    void addTags(List<Integer> tagIds, Long id);

    List<String> getTagsByFoodId(Integer foodId);
}
