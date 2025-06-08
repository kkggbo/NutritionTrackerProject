package com.nt.tracker;

import com.nt.tracker.common.Result;
import com.nt.tracker.domain.dto.FoodDTO;
import com.nt.tracker.domain.dto.User;
import com.nt.tracker.domain.vo.FoodVO;
import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.service.FoodService;
import com.nt.tracker.utils.JwtUtils;
import com.nt.tracker.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class MyTest {
    @Autowired
    private FoodService foodService;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    RedisUtils redisUtils;

    @Test
    void  test() {
        Map<String, Object> claims = JwtUtils.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiJLS0dHQk8iLCJleHAiOjE3NDgxMTkzMzB9.4p-gooI57Z_pHs3fMjqP61c1rPgTpynk7H6GjpNdqEE");
        // log.info("{}", claims);
        System.out.println(claims);
    }

    @Test
    void test2() {
        for (long i = 4; i <= 14; i++) {
            FoodVO food = foodMapper.getFoodById(i);
            if (food != null){
                FoodDTO foodDTO = new FoodDTO();
                BeanUtils.copyProperties(food, foodDTO);
                foodService.addTags(foodDTO);
            }
        }
    }

    @Test
    void redisTest() {
        User user = new User(777L, "testUser777", "1234567890777");
        redisUtils.set("testUser2", user);
        System.out.println(redisUtils.get("testUser2", User.class));
    }
}
