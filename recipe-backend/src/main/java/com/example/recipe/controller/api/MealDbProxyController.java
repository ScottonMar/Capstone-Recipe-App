package com.example.recipe.controller.api;

import com.example.recipe.model.Recipe;
import com.example.recipe.service.MealDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mealdb")
public class MealDbProxyController {

    private final MealDbService mealDb;

    @GetMapping("/search")
    public List<Recipe> search(@RequestParam String q) {
        return mealDb.searchByName(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> byId(@PathVariable String id) {
        Recipe r = mealDb.getById(id);
        return r == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }

    @GetMapping("/ingredients")
    public List<String> ingredients(@RequestParam(value = "q", required = false) String q) {
        return mealDb.listIngredients(q);
    }
}
