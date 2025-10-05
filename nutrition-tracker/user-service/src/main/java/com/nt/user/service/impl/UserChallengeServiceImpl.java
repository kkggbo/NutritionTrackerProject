package com.nt.user.service.impl;

import cn.hutool.Hutool;
import com.nt.common.Result;
import com.nt.common.utils.UserThreadLocal;
import com.nt.user.domain.po.UserChallenge;
import com.nt.user.domain.vo.ChallengeVO;
import com.nt.user.domain.vo.UserChallengeVO;
import com.nt.user.mapper.UserChallengeMapper;
import com.nt.user.service.UserChallengeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class UserChallengeServiceImpl implements UserChallengeService {

    @Autowired
    UserChallengeMapper challengeMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public Result<String> startChallenge(Long challengeId) {
        // 获取用户ID
        Long userId = UserThreadLocal.getUserId();

        // 检查用户是否正在进行此挑战
        UserChallengeVO existing = challengeMapper.selectOngoingChallengeByChallengeIdAndUserId(userId, challengeId);
        if (existing != null) {
            return Result.error("您已在进行此挑战");
        }

        // 获取挑战详情
        ChallengeVO challenge = challengeMapper.selectById(challengeId);
        if (challenge == null) {
            return Result.error("挑战不存在");
        }

        // 创建用户挑战实体
        UserChallengeVO challengeEntity = new UserChallengeVO();

        // 填充内容
        challengeEntity.setChallengeId(challengeId);
        challengeEntity.setUserId(userId);
        challengeEntity.setStartTime(LocalDateTime.now());
        challengeEntity.setEndTime(challengeEntity.getStartTime().plusSeconds(challenge.getDurationSeconds()));
        challengeEntity.setStatus("ONGOING");

        // 插入数据库
        int ifInserted = challengeMapper.insert(challengeEntity);

        if (ifInserted > 0) {
            // 计算延迟时间
            Duration duration = Duration.between(challengeEntity.getStartTime(), challengeEntity.getEndTime());
            long delayMillis = duration.toMillis();

            // 向死信队列发送消息
            rabbitTemplate.convertAndSend(
                    "challenge-delay-exchange",
                    "challenge.delay.routingKey",
                    challengeEntity.getId(),
                    msg -> {
                        msg.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                        return msg;
                    }
            );

            return Result.success("挑战已开始");
        } else {
            return Result.error("创建挑战失败");
        }
    }

    @Override
    public Result<String> terminateChallenge(Long userChallengeId) {
        // 获取用户ID
        Long userId = UserThreadLocal.getUserId();

        // 获取当前挑战
        UserChallengeVO challenge = challengeMapper.selectOngoingChallengeByUserChallengeId(userChallengeId);

        if (challenge == null) {
            return Result.error("您未进行此挑战或挑战已结束");
        } else {
            // 终止该挑战
            int rows = challengeMapper.terminateOngoingChallenge(userChallengeId);
            if(rows > 0){
                return Result.success("挑战已终止");
            } else {
                return Result.error("终止挑战失败，请稍后再试");
            }
        }
    }

    @Override
    public Result<List<ChallengeVO>> getAllActiveChallenges() {
        List<ChallengeVO> challenges = challengeMapper.listActiveChallenges();
        return Result.success(challenges);
    }

    @Override
    public Result<List<UserChallengeVO>> getUserChallenges() {
        Long userId = UserThreadLocal.getUserId();
        List<UserChallengeVO> challenges = challengeMapper.getUserOngoingChallengesByUserId(userId);
        return Result.success(challenges);
    }

}

