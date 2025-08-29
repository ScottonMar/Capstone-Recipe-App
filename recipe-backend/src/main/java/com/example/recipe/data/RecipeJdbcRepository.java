package com.example.recipe.data; // ← CHANGE to your package

import com.example.recipe.model.Recipe;
import com.example.recipe.model.RecipeIngredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Repository
public class RecipeJdbcRepository {

    private final JdbcTemplate jdbc;

    public RecipeJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READS
    // ─────────────────────────────────────────────────────────────────────────

    /** Fast list (no children) ordered newest first. */
    public List<Recipe> findAll() {
        return jdbc.query("""
                SELECT recipe_id, external_id, title, image_url, cook_minutes, servings
                FROM recipe
                ORDER BY recipe_id DESC
                """,
                (rs, i) -> {
                    Recipe r = new Recipe();
                    r.setId(rs.getInt("recipe_id"));
                    r.setExternalId(rs.getString("external_id"));
                    r.setTitle(rs.getString("title"));
                    r.setImageUrl(rs.getString("image_url"));
                    r.setCookMinutes(rs.getInt("cook_minutes"));
                    r.setServings(rs.getInt("servings"));
                    return r;
                });
    }

    /** Full aggregate: root + ingredients + steps. Returns null if not found. */
    public Recipe findById(int id) {
        List<Recipe> roots = jdbc.query("""
                SELECT recipe_id, external_id, title, image_url, cook_minutes, servings
                FROM recipe
                WHERE recipe_id = ?
                """,
                (rs, i) -> {
                    Recipe r = new Recipe();
                    r.setId(rs.getInt("recipe_id"));
                    r.setExternalId(rs.getString("external_id"));
                    r.setTitle(rs.getString("title"));
                    r.setImageUrl(rs.getString("image_url"));
                    r.setCookMinutes(rs.getInt("cook_minutes"));
                    r.setServings(rs.getInt("servings"));
                    return r;
                }, id);

        if (roots.isEmpty()) return null;
        Recipe r = roots.get(0);

        r.setIngredients(findIngredients(id));
        r.setSteps(findSteps(id));

        return r;
    }

    /** Return all ingredients for a recipe (ordered by insert id). */
    public List<RecipeIngredient> findIngredients(int recipeId) {
        List<RecipeIngredient> list = jdbc.query("""
                SELECT name, unit, quantity
                FROM recipe_ingredient
                WHERE recipe_id = ?
                ORDER BY recipe_ingredient_id
                """,
                (rs, i) -> new RecipeIngredient(
                        rs.getString("name"),
                        rs.getString("unit"),
                        rs.getBigDecimal("quantity") == null ? 0.0 : rs.getBigDecimal("quantity").doubleValue()
                ),
                recipeId
        );
        return list != null ? list : new ArrayList<>();
    }

    /** Return all steps for a recipe (ordered by step number). */
    public List<String> findSteps(int recipeId) {
        List<String> list = jdbc.query("""
                SELECT instruction
                FROM recipe_step
                WHERE recipe_id = ?
                ORDER BY step_number
                """,
                (rs, i) -> rs.getString("instruction"),
                recipeId
        );
        return list != null ? list : new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITES
    // ─────────────────────────────────────────────────────────────────────────

    /** Insert the root row and return generated recipe_id. */
    public int insertRecipe(Recipe r) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    INSERT INTO recipe (external_id, title, image_url, cook_minutes, servings)
                    VALUES (?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nullToEmpty(r.getExternalId()));
            ps.setString(2, nullToEmpty(r.getTitle()));
            ps.setString(3, nullToEmpty(r.getImageUrl()));
            ps.setInt(4, safeInt(r.getCookMinutes()));
            ps.setInt(5, safeInt(r.getServings()));
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    /** Batch-insert ingredients. No-op if list is null/empty. */
    public void insertIngredients(int recipeId, List<RecipeIngredient> list) {
        if (list == null || list.isEmpty()) return;
        jdbc.batchUpdate("""
                        INSERT INTO recipe_ingredient (recipe_id, name, unit, quantity)
                        VALUES (?, ?, ?, ?)
                        """,
                list,
                100,
                (ps, ing) -> {
                    ps.setInt(1, recipeId);
                    ps.setString(2, nullToEmpty(ing.getName()));
                    ps.setString(3, nullToEmpty(ing.getUnit()));
                    ps.setBigDecimal(4, BigDecimal.valueOf(safeDouble(ing.getQuantity())));
                });
    }

    /** Batch-insert ordered steps. No-op if list is null/empty. */
    public void insertSteps(int recipeId, List<String> steps) {
        if (steps == null || steps.isEmpty()) return;
        List<Map.Entry<Integer, String>> rows =
                IntStream.range(0, steps.size())
                        .mapToObj(i -> Map.entry(i + 1, steps.get(i)))
                        .toList();

        jdbc.batchUpdate("""
                        INSERT INTO recipe_step (recipe_id, step_number, instruction)
                        VALUES (?, ?, ?)
                        """,
                rows,
                100,
                (ps, e) -> {
                    ps.setInt(1, recipeId);
                    ps.setInt(2, e.getKey());
                    ps.setString(3, nullToEmpty(e.getValue()));
                });
    }

    /** Update root fields only (children handled separately). */
    public void updateRecipe(int id, Recipe r) {
        jdbc.update("""
                UPDATE recipe
                SET title = ?, image_url = ?, cook_minutes = ?, servings = ?
                WHERE recipe_id = ?
                """,
                nullToEmpty(r.getTitle()),
                nullToEmpty(r.getImageUrl()),
                safeInt(r.getCookMinutes()),
                safeInt(r.getServings()),
                id
        );
    }

    /** Remove all children (ingredients & steps) for a recipe. */
    public void deleteChildren(int recipeId) {
        jdbc.update("DELETE FROM recipe_ingredient WHERE recipe_id = ?", recipeId);
        jdbc.update("DELETE FROM recipe_step WHERE recipe_id = ?", recipeId);
    }

    /** Delete the root recipe (children cascade via FK ON DELETE CASCADE). */
    public void deleteRecipe(int id) {
        jdbc.update("DELETE FROM recipe WHERE recipe_id = ?", id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
    private static int safeInt(Integer i) {
        return i == null ? 0 : i;
    }
    private static double safeDouble(Double d) {
        return d == null ? 0.0 : d;
    }
}
