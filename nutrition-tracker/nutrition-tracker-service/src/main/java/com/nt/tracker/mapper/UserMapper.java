package com.nt.tracker.mapper;

import com.nt.tracker.domain.dto.UserDTO;
import com.nt.tracker.domain.dto.UserProfileDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Insert("insert into user_profile (user_id, gender, age, height, weight) values (#{userId}, #{gender}, #{age}, #{height}, #{weight})")
    void insertUserProfile(UserProfileDTO userProfile);


    @Select("select * from user where id = #{id}")
    UserDTO getUserById(Long id);

    @Select("select * from user_profile where user_id = #{userId}")
    UserProfileDTO getUserProfile(Long userId);
}
