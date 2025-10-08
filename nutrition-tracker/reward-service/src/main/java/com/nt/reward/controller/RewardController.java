package com.nt.reward.controller;

import com.nt.common.Result;
import com.nt.reward.domain.dto.ExchangeRequestDTO;
import com.nt.reward.domain.po.Gift;
import com.nt.reward.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reward")
public class RewardController {

    @Autowired
    RewardService rewardService;

    @PostMapping("/exchange")
    public Result<String> exchange(@RequestBody ExchangeRequestDTO request) {
        return rewardService.exchange(request);
    }

    @PostMapping("/list")
    public Result<List<Gift>> listGifts() {
        return rewardService.listGifts();
    }

}
