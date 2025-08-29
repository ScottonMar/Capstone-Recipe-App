package com.example.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // scans com.example.recipe.* by default
public class RecipeBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeBackendApplication.class, args);
    }
}
