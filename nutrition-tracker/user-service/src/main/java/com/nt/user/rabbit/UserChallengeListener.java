package com.nt.user.rabbit;

import com.nt.user.domain.po.UserChallenge;
import com.nt.user.mapper.UserChallengeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserChallengeListener {
    @Autowired
    private UserChallengeMapper challengeMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 监听 challenge-delay-queue 队列，处理挑战延迟消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "challenge-dlx-queue", durable = "true"),
            exchange = @Exchange(value = "challenge-dlx-exchange"),
            key = "challenge.delay.routingKey"
    ))
    public void handleChallengeDelay(Long challengeId) {
        UserChallenge challenge = challengeMapper.selectById(challengeId);
        if (challenge == null || !"ONGOING".equals(challenge.getStatus())) {
            log.info("挑战已结束或不存在，ID={}", challengeId);
            return;
        }
        challenge.setStatus("SUCCESS");
        challenge.setSettleTime(LocalDateTime.now());
        challengeMapper.updateById(challenge);
        // 获取挑战的积分和用户id
        int rewardPoints = challenge.getRewardPoints();
        Long userId = challenge.getUserId();
        // 发送消息给用户交换机，添加积分
        String message = userId + ":" + rewardPoints;
        rabbitTemplate.convertAndSend("user-exchange", "add-points", message);
        System.out.println("Sent message: " + message);

        log.info("挑战结算完成，ID={}，状态={}，用户={}，获得的积分={}", challengeId, challenge.getStatus(), userId, rewardPoints);
    }
}
