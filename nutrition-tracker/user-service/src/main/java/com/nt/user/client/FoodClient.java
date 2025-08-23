package com.nt.user.client;

import com.nt.user.domain.po.IntakePO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "food-service")
public interface FoodClient {
    @GetMapping("/food/intakeOfDay")
    List<IntakePO> getIntakeOfDay(@RequestParam Long userId, @RequestParam LocalDate date);
}
