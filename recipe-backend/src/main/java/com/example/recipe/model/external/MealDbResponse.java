package com.example.recipe.model.external;

import lombok.Data;
import java.util.List;

@Data
public class MealDbResponse {
    private List<MealDbMeal> meals;
}
