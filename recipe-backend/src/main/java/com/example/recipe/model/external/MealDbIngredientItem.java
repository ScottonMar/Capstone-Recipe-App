package com.example.recipe.model.external;

import lombok.Data;

@Data
public class MealDbIngredientItem {
    private String idIngredient;
    private String strIngredient;
    private String strDescription;  // sometimes present
    private String strType;         // optional
}
