package com.example.recipe.controller.api;

import com.example.recipe.model.Recipe;
import com.example.recipe.service.RecipePersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeDbController {

    private final RecipePersistenceService svc;

    public RecipeDbController(RecipePersistenceService svc) {
        this.svc = svc;
    }

    /**
     * List all saved recipes (summary fields are fine: id/title/cookMinutes/servings/imageUrl).
     */
    @GetMapping
    public List<Recipe> all() {
        return svc.all();
    }

    /**
     * Get a single saved recipe (with ingredients/steps).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> get(@PathVariable int id) {
        Recipe r = svc.get(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    /**
     * Save a recipe coming from the external MealDB detail view.
     * Returns 201 with Location: /api/recipes/{newId}
     */
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody Recipe r) {
        int id = svc.saveFromExternal(r);
        return ResponseEntity.created(URI.create("/api/recipes/" + id)).build();
    }

    /**
     * Update an existing recipe. Clears and re-inserts children (ingredients/steps).
     * Returns 204 if updated, 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Recipe r) {
        if (svc.get(id) == null) return ResponseEntity.notFound().build();
        svc.update(id, r);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a saved recipe. Returns 204, or 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (svc.get(id) == null) return ResponseEntity.notFound().build();
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
