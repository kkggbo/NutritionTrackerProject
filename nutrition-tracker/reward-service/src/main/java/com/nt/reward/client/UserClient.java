package com.nt.reward.client;

import com.nt.common.Result;
import com.nt.common.domain.vo.FoodVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    /**
     * 扣减用户积分
     * @param userId 用户ID
     * @param points 扣减积分数量
     * @return 扣减是否成功
     */
    @PostMapping("/user/{userId}/points/deduct")
    boolean deductPoints(@PathVariable("userId") Long userId,
                         @RequestParam("points") Integer points);
}
