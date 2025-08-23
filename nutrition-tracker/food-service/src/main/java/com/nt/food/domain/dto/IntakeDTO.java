package com.nt.food.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class IntakeDTO implements Serializable {
    private Integer mealType;
    private Long foodId;
    private Double weight;
}
