package edu.cit.peerreads.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import edu.cit.peerreads.backend.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BackendApplication {

	static {
		// Explicitly set Supabase database connection properties
		// This runs BEFORE Spring Boot initializes, ensuring properties are available
		
		// Default Supabase connection values (using direct connection for better transaction support)
		String defaultUrl = "jdbc:postgresql://aws-1-ap-southeast-2.supabase.co:5432/postgres?sslmode=require";
		String defaultUsername = "postgres.ztetplqgjemxswqksnhm";
		String defaultPassword = "Alexandrei.1626";
		
		// Get environment variables (from render.yaml or system)
		String envUrl = System.getenv("SPRING_DATASOURCE_URL");
		String envUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
		String envPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
		
		// Process URL
		String finalUrl = defaultUrl;
		if (envUrl != null && !envUrl.trim().isEmpty()) {
			System.out.println("🔍 Detected SPRING_DATASOURCE_URL: " + (envUrl.length() > 60 ? envUrl.substring(0, 60) + "..." : envUrl));
			
			if (envUrl.startsWith("jdbc:postgresql://")) {
				finalUrl = envUrl;
				System.out.println("✓ Using JDBC connection string from environment");
			} else if (envUrl.startsWith("postgresql://")) {
				try {
					String url = envUrl.replace("postgresql://", "");
					int atIndex = url.indexOf("@");
					if (atIndex > 0) {
						url = url.substring(atIndex + 1);
					}
					finalUrl = "jdbc:postgresql://" + url;
					System.out.println("✓ Converted postgresql:// to JDBC format");
				} catch (Exception e) {
					System.err.println("⚠ Failed to convert URL, using Supabase default: " + e.getMessage());
				}
			} else {
				System.err.println("⚠ Invalid URL format, using Supabase default");
			}
		} else {
			System.out.println("ℹ Using Supabase default connection URL");
		}
		
		// Set final properties (environment variables override defaults)
		System.setProperty("spring.datasource.url", finalUrl);
		System.setProperty("spring.datasource.username", envUsername != null && !envUsername.trim().isEmpty() ? envUsername : defaultUsername);
		System.setProperty("spring.datasource.password", envPassword != null && !envPassword.trim().isEmpty() ? envPassword : defaultPassword);
		System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
		
		System.out.println("✓ Database connection properties configured");
		System.out.println("  URL: " + (finalUrl.length() > 60 ? finalUrl.substring(0, 60) + "..." : finalUrl));
		System.out.println("  Username: " + (envUsername != null && !envUsername.trim().isEmpty() ? envUsername : defaultUsername));
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
