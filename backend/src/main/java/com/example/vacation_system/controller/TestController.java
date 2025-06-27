package com.example.vacation_system.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Vacation Management System is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/database")
    public Map<String, String> databaseCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("database", "PostgreSQL");
        response.put("status", "Connected");
        response.put("message", "Database connection is working");
        return response;
    }
}
