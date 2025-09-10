package com.nt.user.mapper;


import com.nt.user.domain.po.UserChallenge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserChallengeMapper {


    void insert(UserChallenge challenge);

    @Select("SELECT * FROM user_challenge WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserChallenge> selectChallengesByUser(Long userId);

    @Select("SELECT * FROM user_challenge WHERE id = #{id}")
    UserChallenge selectById(Long id);

    void updateById(UserChallenge challenge);
}
