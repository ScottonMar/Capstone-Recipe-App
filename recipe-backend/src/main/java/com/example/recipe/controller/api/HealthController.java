package com.example.recipe.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbc;

    @GetMapping("/api/health/db")
    public Map<String, Object> dbHealth() {
        Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
        Integer users = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return Map.of("select1", one, "usersCount", users);
    }
}
