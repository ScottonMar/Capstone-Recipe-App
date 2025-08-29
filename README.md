# Capstone-Recipe-App
# 🍲 Recipe App — Full Stack Capstone Project

## 📖 Overview
The **Recipe App** is a full-stack web application that allows users to:
- Search recipes from the [TheMealDB API](https://www.themealdb.com/api.php).
- View recipe details (ingredients, instructions, images).
- Save favorite recipes into a **MySQL database**.
- Edit or delete saved recipes (full CRUD support).
- Enjoy a stretch goal: **live image preview** when editing recipes.

This project demonstrates my skills in **Java Spring Boot, MySQL, and React** as part of a Full Stack Capstone assessment.

---

## 🛠 Tech Stack

### Frontend
- React (Vite)  
- JavaScript / HTML / CSS  
- React Router (routing)  
- Axios (API calls)  

### Backend
- Java 17  
- Spring Boot (MVC + REST)  
- Spring JDBC / JdbcTemplate  
- Maven (build & dependencies)  

### Database
- MySQL 8.x  
- Tables: `recipe`, `recipe_ingredient`, `recipe_step`  

### APIs & Tools
- TheMealDB (public API)  
- Postman (API testing)  
- JUnit 5 (backend tests)  
- Mermaid (diagrams)  
- GitHub (version control & docs)

---

## 🏗 System Architecture

```mermaid
flowchart LR
  U[User] --> R[React App]
  R -->|Axios / HTTP| API[Spring Boot REST API]
  API --> S[Service Layer]
  S --> REPO[JDBC Repository]
  REPO --> DB[(MySQL Database)]
  API -.-> X[(TheMealDB API)]
