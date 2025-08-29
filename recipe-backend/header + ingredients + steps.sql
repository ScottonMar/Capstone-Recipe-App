USE recipe_app;

-- ─────────────────────────────────────────────────────────────────────────────
-- Pick which recipe to show
-- Option A (default): latest saved recipe
SET @rid := (SELECT recipe_id FROM recipe ORDER BY recipe_id DESC LIMIT 1);

-- Option B: uncomment and set an explicit id, e.g. 7
-- SET @rid := 7;
-- ─────────────────────────────────────────────────────────────────────────────

-- 1) Header: core recipe info
SELECT 
  r.recipe_id, r.title, r.image_url, r.cook_minutes, r.servings
FROM recipe r
WHERE r.recipe_id = @rid;

-- 2) Combined details: ingredients and steps in one table (nicely ordered)
SELECT section, item_order, name, unit, quantity, instruction
FROM (
  -- Ingredients
  SELECT 
    'INGREDIENT' AS section,
    ROW_NUMBER() OVER (ORDER BY ri.name) AS item_order,
    ri.name,
    ri.unit,
    ri.quantity,
    NULL AS instruction
  FROM recipe_ingredient ri
  WHERE ri.recipe_id = @rid

  UNION ALL

  -- Steps
  SELECT 
    'STEP' AS section,
    rs.step_number AS item_order,
    NULL AS name,
    NULL AS unit,
    NULL AS quantity,
    rs.instruction
  FROM recipe_step rs
  WHERE rs.recipe_id = @rid
) x
ORDER BY 
  CASE section WHEN 'INGREDIENT' THEN 1 ELSE 2 END,
  item_order;
