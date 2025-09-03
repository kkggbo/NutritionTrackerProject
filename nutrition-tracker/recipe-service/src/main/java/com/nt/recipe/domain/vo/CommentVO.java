package com.nt.recipe.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentVO implements Serializable {
    private Long id;             // 评论ID
    private Long recipeId;       // 食谱ID
    private Long userId;         // 用户ID
    // private String userName;     // 用户名（可用于显示）
    private String content;      // 评论内容
    private LocalDateTime createTime; // 评论时间
}
