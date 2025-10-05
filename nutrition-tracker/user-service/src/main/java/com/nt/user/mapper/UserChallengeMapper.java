package com.nt.user.mapper;


import com.nt.user.domain.po.UserChallenge;
import com.nt.user.domain.vo.ChallengeVO;
import com.nt.user.domain.vo.UserChallengeVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserChallengeMapper {
    // 查询进行中的挑战
    @Select("SELECT * FROM user_challenge WHERE user_id = #{userId} AND challenge_id = #{challengeId} AND status = 'ONGOING'")
    UserChallengeVO selectOngoingChallengeByChallengeIdAndUserId(Long userId, Long challengeId);

    @Select("SELECT * FROM user_challenge WHERE id = #{userChallengeId} AND status = 'ONGOING'")
    UserChallengeVO selectOngoingChallengeByUserChallengeId(Long userChallengeId);


    // 查询挑战详情
    @Select("SELECT * FROM challenges WHERE id = #{id}")
    ChallengeVO selectById(Long id);


    // 插入新的挑战记录
    @Insert("INSERT INTO user_challenge (challenge_id, user_id, start_time, end_time, status) " +
            "VALUES (#{challengeId}, #{userId}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserChallengeVO challengeEntity);

    // 终止进行中的挑战
    @Update("UPDATE user_challenge SET status = 'CANCELLED', terminate_time = NOW() " +
            "WHERE id = #{userChallengeId} AND status = 'ONGOING'")
    int terminateOngoingChallenge(Long userChallengeId);

    // 获取所有可进行的挑战
    @Select("SELECT id, title, description, duration_seconds, reward_points, create_time, update_time, is_active " +
            "FROM challenges " +
            "WHERE is_active = 1")
    List<ChallengeVO> listActiveChallenges();

    // 获取用户进行中的挑战
    @Select("SELECT id, challenge_id, user_id, start_time, end_time, status, terminate_time, create_time, update_time " +
            "FROM user_challenge " +
            "WHERE user_id = #{userId} AND status = 'ONGOING'")
    List<UserChallengeVO> getUserOngoingChallengesByUserId(Long userId);

    @Update("UPDATE user_challenge " +
            "SET status = #{status}, update_time = NOW() " +
            "WHERE id = #{id} AND status = 'ONGOING'")
    void updateUserChallengeById(UserChallengeVO userChallenge);

    @Select("SELECT id, title, description, duration_seconds AS durationSeconds, reward_points AS rewardPoints, is_active AS isActive " +
            "FROM challenges " +
            "WHERE id = #{challengeId}")
    ChallengeVO selectChallengeById(Long challengeId);
}
