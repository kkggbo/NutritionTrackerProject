package com.nt.tracker;

import com.nt.tracker.mapper.FoodMapper;
import com.nt.tracker.service.FoodService;
import com.nt.tracker.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class MyTest {
    @Autowired
    private FoodService foodService;
    @Autowired
    private FoodMapper foodMapper;

    @Test
    void  test() {
        Map<String, Object> claims = JwtUtils.parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiJLS0dHQk8iLCJleHAiOjE3NDgxMTkzMzB9.4p-gooI57Z_pHs3fMjqP61c1rPgTpynk7H6GjpNdqEE");
        // log.info("{}", claims);
        System.out.println(claims);
    }

    @Test
    void test2() {}
}
