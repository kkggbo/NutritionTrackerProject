package com.nt.user.mapper;

import com.nt.user.domain.dto.UserDTO;
import com.nt.user.domain.dto.UserProfileDTO;
import com.nt.user.domain.po.UserProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper {
    @Insert("insert into user_profile (user_id, user_name, gender, birth_year, height, weight, goal, activity_level, bmi, bmr, daily_calories) values (#{userId}, #{userName}, #{gender}, #{birthYear}, #{height}, #{weight}, #{goal}, #{activityLevel}, #{bmi}, #{bmr}, #{dailyCalories})")
    void insertUserProfile(UserProfile userProfile);


    @Select("select * from user where id = #{id}")
    UserDTO getUserById(Long id);

    @Select("select user_name, gender, YEAR(CURDATE()) - birth_year AS age, height, weight, goal, activity_level from user_profile where user_id = #{userId}")
    UserProfileDTO getUserProfile(Long userId);

    @Select("select daily_calories from user_profile where user_id = #{userId}")
    int getDailyCalories(Long userId);


    void updateUserProfile(UserProfile userProfile);

    @Update("UPDATE user SET points = points + #{points} WHERE id = #{userId}")
    void increasePoints(Long userId, Integer points);

    @Select("SELECT username FROM user WHERE id = #{userId}")
    String getUserNameById(Long userId);
}
