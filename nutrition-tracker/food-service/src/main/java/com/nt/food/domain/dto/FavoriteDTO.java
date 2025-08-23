package com.nt.food.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FavoriteDTO implements Serializable {
    private Long foodId;
    private Boolean favorite;
}