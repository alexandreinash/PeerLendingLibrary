package edu.cit.peerreads.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import edu.cit.peerreads.backend.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BackendApplication {

	static {
		// Convert Render's postgresql:// connection string to jdbc:postgresql:// format
		// This runs BEFORE Spring Boot initializes, so it can modify environment variables
		// Only processes if URL starts with postgresql:// (not jdbc:postgresql://)
		String envUrl = System.getenv("SPRING_DATASOURCE_URL");
		if (envUrl != null && envUrl.startsWith("postgresql://") && !envUrl.startsWith("jdbc:")) {
			try {
				// Remove postgresql:// prefix
				String url = envUrl.replace("postgresql://", "");
				
				// Extract parts: user:pass@host:port/db
				int atIndex = url.indexOf("@");
				if (atIndex > 0) {
					// Remove user:pass@ part
					url = url.substring(atIndex + 1);
				}
				
				// Add jdbc:postgresql:// prefix and set as system property
				String jdbcUrl = "jdbc:postgresql://" + url;
				System.setProperty("SPRING_DATASOURCE_URL", jdbcUrl);
				System.out.println("✓ Converted PostgreSQL connection string to JDBC format");
			} catch (Exception e) {
				System.err.println("⚠ Warning: Failed to convert connection string: " + e.getMessage());
				// Continue with original URL
			}
		} else if (envUrl != null && envUrl.startsWith("jdbc:postgresql://")) {
			System.out.println("✓ Using JDBC connection string (Supabase)");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
