package edu.cit.peerreads.backend.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Always return UP without checking database
        // This ensures health check responds immediately
        return Health.up()
                .withDetail("status", "UP")
                .withDetail("message", "Application is running")
                .build();
    }
}

