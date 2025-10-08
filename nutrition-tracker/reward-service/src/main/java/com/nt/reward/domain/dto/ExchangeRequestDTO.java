package com.nt.reward.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExchangeRequestDTO implements Serializable {

    private Long giftId;    // 兑换的礼品id
    private Integer count;  // 兑换的礼品数量
}
