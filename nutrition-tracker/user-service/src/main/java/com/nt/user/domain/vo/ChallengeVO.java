package com.nt.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeVO {
    private Long id;                 // 挑战ID
    private String title;            // 挑战标题
    private String description;      // 挑战描述
    private Long durationSeconds;    // 挑战时长（秒）
    private Integer rewardPoints;    // 奖励积分
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isActive;        // 是否激活

}