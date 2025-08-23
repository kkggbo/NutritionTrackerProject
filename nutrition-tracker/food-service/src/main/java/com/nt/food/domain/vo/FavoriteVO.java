package com.nt.food.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FavoriteVO implements Serializable {
    private Boolean favorite;
}
