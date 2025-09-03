package com.nt.recipe.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentPO implements Serializable {
    private Long id;             // 评论ID
    private Long recipeId;       // 食谱ID
    private Long userId;         // 用户ID
    private String content;      // 评论内容
    private LocalDateTime createTime;   // 评论时间
}
