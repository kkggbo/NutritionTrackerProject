package com.nt.user.service.impl;

import com.nt.common.utils.UserThreadLocal;
import com.nt.user.domain.po.UserChallenge;
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
    public UserChallenge startChallenge(UserChallenge challenge) {
        challenge.setUserId(UserThreadLocal.getUserId());
        challenge.setStatus("ONGOING");
        challengeMapper.insert(challenge);

        // 计算延迟时间
        Duration duration = Duration.between(challenge.getStartTime(), challenge.getEndTime());
        long delayMillis = duration.toMillis();

        rabbitTemplate.convertAndSend(
                "challenge-delay-exchange",
                "challenge.delay.routingKey",
                challenge.getId(),
                msg -> {
                    msg.getMessageProperties().setExpiration(String.valueOf(delayMillis));
                    return msg;
                }
        );

        return challenge;
    }

    @Override
    public List<UserChallenge> getChallengesByUser(Long userId) {
        return challengeMapper.selectChallengesByUser(userId);
    }

    @Override
    public UserChallenge getChallengeDetail(Long id) {
        return challengeMapper.selectById(id);
    }

    @Override
    public UserChallenge terminateChallenge(Long id) {
        UserChallenge challenge = challengeMapper.selectById(id);
        if (challenge != null && "ONGOING".equals(challenge.getStatus())) {
            challenge.setStatus("TERMINATED");
            challenge.setSettleTime(LocalDateTime.now());
            challengeMapper.updateById(challenge);
        }
        return challenge;
    }

}

