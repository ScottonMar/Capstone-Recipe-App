package com.example.recipe.model;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {
    private Integer id;
    private String title;
    private String imageUrl;
    private int cookMinutes;
    private int servings;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;

    public void setId(String externalId) {
    }

    public void setId(int recipeId) {
    }

    public void setExternalId(String externalId) {
    }

    public String getExternalId() {
        return "";
    }

}
