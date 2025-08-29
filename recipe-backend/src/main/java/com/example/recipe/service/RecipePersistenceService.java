package com.example.recipe.service;

import com.example.recipe.model.Recipe;
import com.example.recipe.model.RecipeIngredient;
import com.example.recipe.data.RecipeJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RecipePersistenceService {

    private final RecipeJdbcRepository repo;

    public RecipePersistenceService(RecipeJdbcRepository repo) {
        this.repo = repo;
    }

    /** List all saved recipes (no children for speed). */
    public List<Recipe> all() {
        return repo.findAll();
    }

    /** Get one recipe with ingredients & steps, or null if not found. */
    public Recipe get(int id) {
        return repo.findById(id);
    }

    /**
     * Save a recipe coming from the external detail page.
     * Inserts root, then children. Returns the new recipe id.
     */
    public int saveFromExternal(Recipe r) {
        if (r == null) throw new IllegalArgumentException("recipe is null");
        int id = repo.insertRecipe(r);

        List<RecipeIngredient> ings = r.getIngredients() == null ? Collections.emptyList() : r.getIngredients();
        if (!ings.isEmpty()) repo.insertIngredients(id, ings);

        List<String> steps = r.getSteps() == null ? Collections.emptyList() : r.getSteps();
        if (!steps.isEmpty()) repo.insertSteps(id, steps);

        return id;
    }

    /**
     * Update root fields, then replace children (clear & re-insert).
     * Caller should 404 if recipe doesn't exist before calling this.
     */
    public void update(int id, Recipe r) {
        if (r == null) throw new IllegalArgumentException("recipe is null");
        repo.updateRecipe(id, r);

        // simplest approach for small child lists: delete & re-insert
        repo.deleteChildren(id);

        List<RecipeIngredient> ings = r.getIngredients() == null ? Collections.emptyList() : r.getIngredients();
        if (!ings.isEmpty()) repo.insertIngredients(id, ings);

        List<String> steps = r.getSteps() == null ? Collections.emptyList() : r.getSteps();
        if (!steps.isEmpty()) repo.insertSteps(id, steps);
    }

    /** Delete a recipe (children cascade via FK). */
    public void delete(int id) {
        repo.deleteRecipe(id);
    }
}
