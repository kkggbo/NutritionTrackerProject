package com.nt.user.rabbit;

import com.nt.user.mapper.UserMapper;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserPointsListener {

    @Autowired
    private UserMapper userMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "user-points-queue", durable = "true"),
            exchange = @Exchange(value = "user-exchange", type = "direct", durable = "true"),
            key = "add-points"
    ))
    public void handleAddPoints(String message) {
        String[] parts = message.split(":");
        Long userId = Long.parseLong(parts[0]);
        Integer points = Integer.parseInt(parts[1]);

        // 给用户加积分
        userMapper.increasePoints(userId, points);
        System.out.println("Added " + points + " points to user " + userId);
    }

}
