package com.nt.food.domain.dto;

import com.nt.food.domain.po.MealFood;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class MealUpdateRequestDTO implements Serializable {
    private int mealType;
    private LocalDate date;
    private List<MealFood> foods;

}
