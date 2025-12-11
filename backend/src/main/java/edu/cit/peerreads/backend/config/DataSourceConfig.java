package edu.cit.peerreads.backend.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * Automatically converts Render's postgresql:// connection string to jdbc:postgresql:// format
 * This handles the case where Render provides postgresql:// but Spring Boot needs jdbc:postgresql://
 */
public class DataSourceConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        
        // Check both environment variable and property
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (datasourceUrl == null) {
            datasourceUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        }
        
        if (datasourceUrl != null && datasourceUrl.startsWith("postgresql://")) {
            // Convert postgresql://user:pass@host:port/db to jdbc:postgresql://host:port/db
            String jdbcUrl = convertToJdbcFormat(datasourceUrl);
            
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);
            
            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addFirst(new MapPropertySource("datasource-url-fix", properties));
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
        
        // Add jdbc:postgresql:// prefix
        return "jdbc:postgresql://" + url;
    }
}

