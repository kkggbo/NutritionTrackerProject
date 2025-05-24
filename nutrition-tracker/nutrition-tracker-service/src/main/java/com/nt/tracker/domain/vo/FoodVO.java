package com.nt.tracker.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FoodVO implements Serializable {
    private Long id;
    private String name;
    private Double caloriesPer100g;
    private Double carbsPer100g;
    private Double proteinPer100g;
    private Double fatPer100g;
}

