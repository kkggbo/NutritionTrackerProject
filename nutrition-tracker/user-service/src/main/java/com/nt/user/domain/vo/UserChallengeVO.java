package com.nt.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeVO {
    private Long id;                  // 实例ID
    private Long challengeId;         // 挑战ID
    private Long userId;              // 用户ID
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private String status;            // 状态 (ONGOING, CANCELLED, SUCCESS)
    private LocalDateTime terminateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}