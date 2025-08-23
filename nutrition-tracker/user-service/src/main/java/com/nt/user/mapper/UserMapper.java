package com.nt.user.mapper;

import com.nt.user.domain.dto.UserDTO;
import com.nt.user.domain.dto.UserProfileDTO;
import com.nt.user.domain.po.UserProfile;
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
