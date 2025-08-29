package com.example.recipe.model;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeIngredient {
    private String name;
    private String unit;
    private double quantity;
}
