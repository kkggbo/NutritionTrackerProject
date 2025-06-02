package com.nt.tracker.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class IntakeDTO implements Serializable {
    private Integer mealType;
    private Long foodId;
    private Double weight;
}
