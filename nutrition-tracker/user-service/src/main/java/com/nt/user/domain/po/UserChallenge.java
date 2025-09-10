package com.nt.user.domain.po;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class UserChallenge implements Serializable {

    private Long id;  // 挑战记录ID

    private Long userId;  // 用户ID

    private String challengeType;  // 挑战类型，如 禁糖、禁食、早睡等

    private LocalDateTime startTime;  // 挑战开始时间

    private LocalDateTime endTime;  // 挑战结束时间

    private String status;  // 挑战状态: ONGOING-进行中, SUCCESS-成功, FAILED-失败, TERMINATED-用户终止

    private Integer rewardPoints;  // 挑战完成可获得的积分

    private LocalDateTime settleTime;  // 挑战结算时间(完成/失败时记录)

}
