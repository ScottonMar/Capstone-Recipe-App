# Recipe App (Java + React)

A full-stack recipe manager. Search public recipes via TheMealDB, view details, and save to your own MySQL-backed collection. Includes edit and delete for saved recipes.

### Tech stack

* Backend: Java 17, Spring Boot (Web, JDBC), MySQL

* Frontend: React (Vite), React Router, Axios

* Stretch goal: Live image preview on the Edit page

## Prerequisites

Java 17 (or 21) + Maven

MySQL 8.x

Node.js 18+ (for the React UI)

## Database Setup


Run schema (in MySQL Workbench: open and execute your schema.sql):

USE recipe_app;
/* tables: users, roles, user_roles (optional), recipe, recipe_ingredient, recipe_step */


(Optional) Seed sample data:

SOURCE /absolute/path/to/data.sql;

## Backend: Run

Configure datasource

recipe-backend/src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/recipe_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=recipe_user
spring.datasource.password=change_me

# JDBCTemplate
spring.sql.init.mode=never
server.port=8080

## Start server

From recipe-backend/:

mvn spring-boot:run

## Key endpoints

Public proxy to TheMealDB

GET /api/mealdb/search?q=chicken

GET /api/mealdb/{id}

Saved recipes (MySQL)

GET /api/recipes — list

GET /api/recipes/{id} — detail

POST /api/recipes/save — save from external result (returns 201 Created with Location)

PUT /api/recipes/{id} — update (returns 204 No Content)

DELETE /api/recipes/{id} — delete (returns 204 No Content)

## Frontend: Run

```From recipe-frontend/:```

npm install

npm run dev


Open: http://localhost:5173

## Vite proxy (ensure this exists)

```recipe-frontend/vite.config.js```

export default {
server: {
port: 5173,
proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }
}
}

### API base

recipe-frontend/src/services/api.js

import axios from 'axios';
export default axios.create({ baseURL: '/api' });

## How to Use (Quick Demo)

1. Search on the home page (e.g., “chicken”).

3. Click a result → Recipe Detail (external).

5. Click Save to My Recipes.

7. Go to My Recipes → open a saved item.

9. Click Edit → update fields or Delete.

## Stretch Goal: Live Image Preview (React)

On the Edit Recipe page, the image preview renders live as you type/paste an Image URL. If the URL fails to load, the preview hides and a small note appears.

* File: recipe-frontend/src/pages/EditRecipe.jsx
 (uses onLoad / onError handlers to set preview state)

* Styles: recipe-frontend/src/index.css

.img-preview { max-width:320px; max-height:240px; border:1px solid #eee; border-radius:6px; display:block; }
.img-preview-wrap { margin-top:8px; }
.img-preview-note { font-size:0.9rem; color:#666; margin-top:4px; }


**Why it’s a stretch:** Adds a dynamic UI enhancement with error handling not covered in the base course.

## Tests (Backend)

```Resources```

src/test/resources/test-schema.sql — drops & recreates schema

src/test/resources/test-data.sql — minimal seed

```Sample tests```

RecipeJdbcRepositoryTest — data layer CRUD round-trip

RecipePersistenceServiceTest — domain layer: save/update workflows

(Optional) RecipeDbControllerTest — MVC smoke test with MockMvc

```Run```
From recipe-backend/:

mvn -q -Dtest=*Recipe* test

## Project Structure (important files)

recipe-backend/

src/main/java/com/example/recipe/
RecipeApplication.java
web/RecipeDbController.java
service/RecipePersistenceService.java
data/RecipeJdbcRepository.java
model/Recipe.java, RecipeIngredient.java
src/main/resources/application.properties
pom.xml

recipe-frontend/
src/
App.jsx
main.jsx
index.css
services/api.js
pages/
RecipeList.jsx
RecipeDetail.jsx          // external result view
MyRecipes.jsx             // saved list
MyRecipeDetail.jsx        // saved detail
EditRecipe.jsx            // edit + stretch goal (image preview)
vite.config.js
package.json

## Troubleshooting

```Blank page on frontend```
Open browser DevTools → Console. Typical fixes:

Wrong import path (case matters): ./pages/RecipeDetail.jsx

Missing default export in a page component (export default …)

CSS pasted into a .jsx file (move styles to index.css)

```CORS / Network errors```
Ensure Vite proxy (/api → http://localhost:8080) and backend is running on 8080.

```MySQL errors```
Verify credentials & schema:

USE recipe_app;
SELECT COUNT(*) FROM recipe;


```POST save is 400```
Your POST body must be a single recipe object (not an array), with fields like:

{"title":"...", "imageUrl":"...", "cookMinutes":0, "servings":0,
"ingredients":[{"name":"Salt","unit":"tsp","quantity":1}],
"steps":["Mix","Bake"]}


## Credits

Data source: TheMealDB

Marquita Scotton (Developer): Full-stack Java + React capstone, 2025
