package com.example.recipe.service;

import com.example.recipe.model.Recipe;
import com.example.recipe.model.RecipeIngredient;
import com.example.recipe.model.external.MealDbMeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealDbMapper {

    public static Recipe toRecipe(MealDbMeal m) {
        var ingredients = extractIngredients(m);
        var steps = splitInstructions(m.getStrInstructions());

        return Recipe.builder()
                .id(Integer.valueOf(m.getIdMeal()))
                .title(m.getStrMeal())
                .imageUrl(m.getStrMealThumb())
                .cookMinutes(0)
                .servings(0)
                .ingredients(ingredients)
                .steps(steps)
                .build();
    }

    private static List<String> splitInstructions(String instructions) {
        if (instructions == null || instructions.isBlank()) return List.of();
        return Arrays.stream(instructions.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static List<RecipeIngredient> extractIngredients(MealDbMeal m) {
        var result = new ArrayList<RecipeIngredient>();
        String[] ing = {
                m.strIngredient1, m.strIngredient2, m.strIngredient3, m.strIngredient4, m.strIngredient5,
                m.strIngredient6, m.strIngredient7, m.strIngredient8, m.strIngredient9, m.strIngredient10,
                m.strIngredient11, m.strIngredient12, m.strIngredient13, m.strIngredient14, m.strIngredient15,
                m.strIngredient16, m.strIngredient17, m.strIngredient18, m.strIngredient19, m.strIngredient20
        };
        String[] meas = {
                m.strMeasure1, m.strMeasure2, m.strMeasure3, m.strMeasure4, m.strMeasure5,
                m.strMeasure6, m.strMeasure7, m.strMeasure8, m.strMeasure9, m.strMeasure10,
                m.strMeasure11, m.strMeasure12, m.strMeasure13, m.strMeasure14, m.strMeasure15,
                m.strMeasure16, m.strMeasure17, m.strMeasure18, m.strMeasure19, m.strMeasure20
        };

        for (int i = 0; i < ing.length; i++) {
            String name = safe(ing[i]);
            String measure = safe(meas[i]);
            if (!name.isEmpty()) {
                double qty = 0;
                String unit = measure;
                try {
                    String[] parts = measure.split("\\s+", 2);
                    if (parts.length > 0) {
                        String numeric = parts[0].replaceAll("[^0-9.]", "");
                        if (!numeric.isEmpty()) qty = Double.parseDouble(numeric);
                        unit = parts.length > 1 ? parts[1] : "";
                    }
                } catch (Exception ignored) {}
                result.add(RecipeIngredient.builder()
                        .name(name)
                        .unit(unit.trim())
                        .quantity(qty)
                        .build());
            }
        }
        return result;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
