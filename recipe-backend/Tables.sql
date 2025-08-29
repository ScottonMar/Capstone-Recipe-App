USE recipe_app;

-- Drop/reset if needed during dev
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS favorite, recipe_step, recipe_ingredient, recipe, user_roles, roles, users;
SET FOREIGN_KEY_CHECKS=1;

-- Users / roles (can remain empty if auth removed)
CREATE TABLE users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE roles (
  role_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Recipes
CREATE TABLE recipe (
  recipe_id INT PRIMARY KEY AUTO_INCREMENT,
  external_id VARCHAR(20),
  title VARCHAR(200) NOT NULL,
  image_url VARCHAR(500),
  cook_minutes INT NOT NULL DEFAULT 0,
  servings INT NOT NULL DEFAULT 0,
  owner_user_id INT NULL,
  FOREIGN KEY (owner_user_id) REFERENCES users(user_id)
);

CREATE TABLE recipe_step (
  recipe_step_id INT PRIMARY KEY AUTO_INCREMENT,
  recipe_id INT NOT NULL,
  step_number INT NOT NULL,
  instruction TEXT NOT NULL,
  FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id) ON DELETE CASCADE
);

CREATE TABLE recipe_ingredient (
  recipe_ingredient_id INT PRIMARY KEY AUTO_INCREMENT,
  recipe_id INT NOT NULL,
  name VARCHAR(200) NOT NULL,
  unit VARCHAR(50) NOT NULL,
  quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
  FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id) ON DELETE CASCADE
);

CREATE TABLE favorite (
  user_id INT NOT NULL,
  recipe_id INT NOT NULL,
  PRIMARY KEY (user_id, recipe_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id) ON DELETE CASCADE
);
