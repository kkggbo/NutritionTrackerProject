package com.nt.recipe.client.fallback;

import com.nt.common.Result;
import com.nt.common.domain.vo.FoodVO;
import com.nt.recipe.client.FoodClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Component
public class FoodClientFallbackFactory implements FallbackFactory<FoodClient> {
    @Override
    public FoodClient create(Throwable cause) {
        return new FoodClient() {
            @Override
            public Result<List<FoodVO>> getFoodsByIds(@RequestBody List<Long> ids){
                log.error("获取食物信息失败，走fallback逻辑", cause);
                return Result.error("获取食物信息失败,服务暂不可用");
            }
        };
    }
}
