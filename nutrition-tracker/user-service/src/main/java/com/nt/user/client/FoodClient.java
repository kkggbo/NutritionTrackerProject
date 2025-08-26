package com.nt.user.client;

import com.nt.user.client.fallback.FoodClientFallbackFactory;
import com.nt.user.domain.po.IntakePO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "food-service", fallbackFactory = FoodClientFallbackFactory.class)
public interface FoodClient {
    @RequestMapping(value = "/food/intakeOfDay", method = RequestMethod.GET)
    List<IntakePO> getIntakeOfDay(@RequestParam LocalDate date);
}
