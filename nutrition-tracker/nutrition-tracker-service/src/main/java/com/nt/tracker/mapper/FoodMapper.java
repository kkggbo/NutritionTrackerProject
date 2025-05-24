package com.nt.tracker.mapper;

import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.IntakeDTO;
import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.vo.FoodVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FoodMapper {


    @Select("select * from food where name = #{name}")
    FoodVO getFoodByName(String name);

    @Insert("insert into food (user_id, name, calories_per_100g, carbs_per_100g, protein_per_100g, fat_per_100g) values (#{userId}, #{name}, #{caloriesPer100g}, #{carbsPer100g}, #{proteinPer100g}, #{fatPer100g})")
    void addFood(FoodDTO food);

    @Select("select * from food where user_id = #{userId}")
    List<FoodVO> getFoodInventory(Long userId);

    @Insert("insert into food_intake (user_id, food_id, intake_date, weight) values (#{userId}, #{foodId}, #{intakeDate}, #{weight})")
    void addIntake(IntakeDTO intakeInfo);

    @Select("select * from food where id = #{foodId}")
    FoodVO getFoodById(Long foodId);

    @Select("select * from food_intake where user_id = #{userId} and intake_date = #{date}")
    List<IntakeDTO> getIntakesByIdAndDate(Long userId, LocalDate date);
}
