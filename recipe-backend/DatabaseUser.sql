CREATE USER IF NOT EXISTS 'recipe_user'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON recipe_app.* TO 'recipe_user'@'localhost';
FLUSH PRIVILEGES;
