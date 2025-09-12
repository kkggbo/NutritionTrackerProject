package com.nt.reward.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RewardOrder implements Serializable{
    private Long id;           // 订单ID
    private Long userId;       // 用户ID
    private Long giftId;       // 礼品ID
    private Integer count;     // 兑换数量
    private Integer totalPoints; // 消耗的总积分
    private Integer status;    // 订单状态: 0=待处理, 1=已发货, 2=已完成, -1=已取消
    private Date createdAt;    // 创建时间
    private Date updatedAt;    // 更新时间
}
