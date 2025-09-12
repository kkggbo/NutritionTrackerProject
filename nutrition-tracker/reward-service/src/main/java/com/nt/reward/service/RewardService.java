package com.nt.reward.service;

import com.nt.common.Result;
import com.nt.reward.domain.dto.ExchangeRequestDTO;

import java.util.List;

public interface RewardService {

    Result<String> exchange(ExchangeRequestDTO request);
}
