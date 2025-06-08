package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FoodDTO implements Serializable {

    private Long id;
    private Long userId;
    private String name;
    private Double caloriesPer100g;
    private Double carbsPer100g;
    private Double proteinPer100g;
    private Double fatPer100g;
}

