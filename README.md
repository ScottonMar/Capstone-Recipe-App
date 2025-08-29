## 🗄 Database Design

### Schema
The database is normalized into three related tables:

- **recipe** → core table (`recipe_id`, `title`, `image_url`, `cook_minutes`, `servings`)  
- **recipe_ingredient** → ingredients linked by `recipe_id`  
- **recipe_step** → step-by-step instructions linked by `recipe_id`  

```mermaid
erDiagram
  recipe ||--o{ recipe_ingredient : contains
  recipe ||--o{ recipe_step : has
  recipe {
    int recipe_id PK
    varchar title
    varchar image_url
    int cook_minutes
    int servings
  }
  recipe_ingredient {
    int recipe_ingredient_id PK
    int recipe_id FK
    varchar name
    varchar unit
    decimal quantity
  }
  recipe_step {
    int recipe_step_id PK
    int recipe_id FK
    int step_number
    text instruction
  }
