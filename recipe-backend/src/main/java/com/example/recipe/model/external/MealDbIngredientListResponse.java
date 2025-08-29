package com.example.recipe.model.external;

import lombok.Data;
import java.util.List;

@Data
public class MealDbIngredientListResponse {
    // TheMealDB returns { "meals": [ {strIngredient: "..."} ] } for lists
    private List<MealDbIngredientItem> meals;
}
