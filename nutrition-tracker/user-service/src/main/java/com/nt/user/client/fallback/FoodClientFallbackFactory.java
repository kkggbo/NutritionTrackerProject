package com.nt.user.client.fallback;

import cn.hutool.core.collection.CollUtil;
import com.nt.user.client.FoodClient;
import com.nt.user.domain.po.IntakePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class FoodClientFallbackFactory implements FallbackFactory<FoodClient> {
    @Override
    public FoodClient create(Throwable cause) {
        return new FoodClient() {
            @Override
            public List<IntakePO> getIntakeOfDay(LocalDate date) {
                log.error("获取用户食物列表失败，走fallback逻辑", cause);
                return CollUtil.empty(List.class);
            }
        };
    }
}
