package com.example.recipe.service.impl;

import com.example.recipe.model.Recipe;
import com.example.recipe.model.external.MealDbMeal;
import com.example.recipe.model.external.MealDbResponse;
import com.example.recipe.model.external.MealDbIngredientItem;
import com.example.recipe.model.external.MealDbIngredientListResponse;
import com.example.recipe.service.MealDbMapper;
import com.example.recipe.service.MealDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealDbServiceImpl implements MealDbService {

    private final RestClient mealDbRestClient;

    @Override
    public List<Recipe> searchByName(String q) {
        MealDbResponse resp = mealDbRestClient.get()
                .uri(uri -> uri.path("/search.php")
                        .queryParam("s", q == null ? "" : q)
                        .build())
                .retrieve()
                .body(MealDbResponse.class);

        if (resp == null || resp.getMeals() == null) return Collections.emptyList();
        return resp.getMeals().stream().map(MealDbMapper::toRecipe).toList();
    }

    @Override
    public Recipe getById(String id) {
        MealDbResponse resp = mealDbRestClient.get()
                .uri(uri -> uri.path("/lookup.php")
                        .queryParam("i", id)
                        .build())
                .retrieve()
                .body(MealDbResponse.class);

        if (resp == null || resp.getMeals() == null || resp.getMeals().isEmpty()) return null;
        MealDbMeal m = resp.getMeals().get(0);
        return MealDbMapper.toRecipe(m);
    }

    @Override
    public List<String> listIngredients(String q) {
        String query = q == null ? "" : q.trim().toLowerCase();

        MealDbIngredientListResponse resp = mealDbRestClient.get()
                .uri(uri -> uri.path("/list.php")
                        .queryParam("i", "list")
                        .build())
                .retrieve()
                .body(MealDbIngredientListResponse.class);

        if (resp == null || resp.getMeals() == null) return List.of();
        return resp.getMeals().stream()
                .map(MealDbIngredientItem::getStrIngredient)
                .filter(name -> name != null && !name.isBlank())
                .filter(name -> query.isEmpty() || name.toLowerCase().contains(query))
                .sorted(String::compareToIgnoreCase)
                .limit(25)
                .toList();
    }
}
