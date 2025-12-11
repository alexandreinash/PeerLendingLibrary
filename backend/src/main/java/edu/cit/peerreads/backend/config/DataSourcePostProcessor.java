package edu.cit.peerreads.backend.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts Render's postgresql:// connection string to jdbc:postgresql:// format
 * This runs early in the Spring Boot startup process
 */
public class DataSourcePostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Get the connection string from environment variable (Render sets this)
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        
        // Also check property sources
        if (datasourceUrl == null || datasourceUrl.isEmpty()) {
            datasourceUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        }
        
        if (datasourceUrl != null && !datasourceUrl.isEmpty()) {
            // Only convert if it's in postgresql:// format
            if (datasourceUrl.startsWith("postgresql://")) {
                try {
                    // Convert postgresql://user:pass@host:port/db to jdbc:postgresql://host:port/db
                    String jdbcUrl = convertToJdbcFormat(datasourceUrl);
                    
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("spring.datasource.url", jdbcUrl);
                    
                    MutablePropertySources propertySources = environment.getPropertySources();
                    propertySources.addFirst(new MapPropertySource("datasource-url-fix", properties));
                    
                    System.out.println("✓ Converted PostgreSQL connection string to JDBC format");
                } catch (Exception e) {
                    System.err.println("✗ Failed to convert connection string: " + e.getMessage());
                    System.err.println("Original URL: " + datasourceUrl);
                    throw new RuntimeException("Database connection string conversion failed", e);
                }
            } else if (datasourceUrl.startsWith("jdbc:postgresql://")) {
                // Already in correct format
                System.out.println("✓ Connection string already in JDBC format");
            }
            // If already in jdbc: format, leave it as is
        }
    }
    
    private String convertToJdbcFormat(String postgresqlUrl) {
        // Remove postgresql:// prefix
        String url = postgresqlUrl.replace("postgresql://", "");
        
        // Extract parts: user:pass@host:port/db
        int atIndex = url.indexOf("@");
        if (atIndex > 0) {
            // Remove user:pass@ part
            url = url.substring(atIndex + 1);
        }
        
        // Validate format
        if (url.isEmpty() || !url.contains(":")) {
            throw new IllegalArgumentException("Invalid PostgreSQL connection string format: " + postgresqlUrl);
        }
        
        // Add jdbc:postgresql:// prefix
        return "jdbc:postgresql://" + url;
    }
}

