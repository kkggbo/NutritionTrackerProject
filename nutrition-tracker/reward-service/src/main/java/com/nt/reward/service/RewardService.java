package com.nt.reward.service;

import com.nt.common.Result;
import com.nt.reward.domain.dto.ExchangeRequestDTO;
import com.nt.reward.domain.po.Gift;

import java.util.List;

public interface RewardService {

    Result<String> exchange(ExchangeRequestDTO request);

    Result<List<Gift>> listGifts();
}
