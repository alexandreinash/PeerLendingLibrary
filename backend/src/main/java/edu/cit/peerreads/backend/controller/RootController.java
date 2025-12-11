package edu.cit.peerreads.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Peer Lending Library API");
        response.put("status", "running");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "health", "/health",
            "actuator", "/actuator/health",
            "api", "/api",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(response);
    }
}

