package com.nt.reward.domain.po;

import lombok.Data;
import java.util.Date;

@Data
public class Gift {
    private Long id;               // 礼品ID
    private String name;           // 礼品名称
    private String description;    // 礼品描述
    private String imageUrl;       // 礼品图片
    private Integer requiredPoints; // 兑换所需积分
    private Integer stock;         // 库存数量
    private Integer status;        // 状态: 1=上架, 0=下架
    private Date createdAt;        // 创建时间
    private Date updatedAt;        // 更新时间
}
