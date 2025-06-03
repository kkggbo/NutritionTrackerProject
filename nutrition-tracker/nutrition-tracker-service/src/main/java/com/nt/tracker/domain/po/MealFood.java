package com.nt.tracker.domain.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class MealFood implements Serializable {
    private String name;
    private Double weight;
    private Double caloriesPer100g;
    private Double carbsPer100g;
    private Double proteinPer100g;
    private Double fatPer100g;
}
