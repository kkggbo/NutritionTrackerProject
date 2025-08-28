package com.nt.recipe.client;

import com.nt.common.Result;
import com.nt.common.domain.vo.FoodVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "food-service")
public interface FoodClient {

    @PostMapping("/food/batch")
    public Result<List<FoodVO>> getFoodsByIds(@RequestBody List<Long> ids);
}
