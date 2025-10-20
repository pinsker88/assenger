package com.example.signal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple REST controller exposing a basic health endpoint. This is useful for
 * container orchestration systems (or your own scripts) to verify that the
 * application is running. It returns a JSON object with a single
 * {@code status} key.
 */
@RestController
public class HealthController {
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}