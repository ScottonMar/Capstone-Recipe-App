package com.example.recipe.service;

import com.example.recipe.model.Recipe;
import java.util.List;

public interface MealDbService {
    List<Recipe> searchByName(String q);
    Recipe getById(String id);
    List<String> listIngredients(String q);
}
