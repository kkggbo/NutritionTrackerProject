package com.nt.tracker.mapper;

import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.dto.UserProfile;
import com.nt.tracker.domain.dto.UserProfileDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Insert("insert into user_profile (user_id, gender, age, height, weight, goal, activity_level, bmi, bmr, daily_calories) values (#{userId}, #{gender}, #{age}, #{height}, #{weight}, #{goal}, #{activityLevel}, #{bmi}, #{bmr}, #{dailyCalories})")
    void insertUserProfile(UserProfile userProfile);


    @Select("select * from user where id = #{id}")
    UserDTO getUserById(Long id);

    @Select("select * from user_profile where user_id = #{userId}")
    UserProfileDTO getUserProfile(Long userId);

    @Select("select daily_calories from user_profile where user_id = #{userId}")
    int getDailyCalories(Long userId);
}
