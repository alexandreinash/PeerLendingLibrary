package edu.cit.peerreads.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * One-time database fix to alter image_url and profile_picture_url columns to MEDIUMTEXT
 * This will run once on application startup
 */
@Slf4j
@Component
@Order(1)
public class DatabaseFix implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public void run(String... args) {
        // Skip database fixes for PostgreSQL (this class is MySQL-specific)
        if (datasourceUrl != null && datasourceUrl.contains("postgresql")) {
            log.info("Skipping MySQL-specific database fixes (using PostgreSQL)");
            return;
        }
        
        try {
            // Extract database name from URL
            String dbUrl = datasourceUrl;
            if (dbUrl.contains("?")) {
                dbUrl = dbUrl.substring(0, dbUrl.indexOf("?"));
            }
            
            // Connect to MySQL
            Connection conn = DriverManager.getConnection(dbUrl, datasourceUsername, datasourcePassword);
            Statement stmt = conn.createStatement();
            
            // Fix books table image_url column
            try {
                stmt.executeUpdate("ALTER TABLE books MODIFY COLUMN image_url MEDIUMTEXT");
                log.info("Successfully altered books.image_url column to MEDIUMTEXT");
            } catch (Exception e) {
                // Column might already be MEDIUMTEXT or error occurred
                log.debug("Could not alter books.image_url column (might already be correct): {}", e.getMessage());
            }
            
            // Fix users table profile_picture_url column
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN profile_picture_url MEDIUMTEXT");
                log.info("Successfully altered users.profile_picture_url column to MEDIUMTEXT");
            } catch (Exception e) {
                // Column might already be MEDIUMTEXT or error occurred
                log.debug("Could not alter users.profile_picture_url column (might already be correct): {}", e.getMessage());
            }
            
            // Fix users table other columns for data truncation
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN full_name VARCHAR(255) NOT NULL");
                log.info("Successfully updated users.full_name column");
            } catch (Exception e) {
                log.debug("Could not alter users.full_name column: {}", e.getMessage());
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NOT NULL");
                log.info("Successfully updated users.email column");
            } catch (Exception e) {
                log.debug("Could not alter users.email column: {}", e.getMessage());
            }
            
            try {
                stmt.executeUpdate("ALTER TABLE users MODIFY COLUMN location VARCHAR(255)");
                log.info("Successfully updated users.location column");
            } catch (Exception e) {
                log.debug("Could not alter users.location column: {}", e.getMessage());
            }
            
            stmt.close();
            conn.close();
        } catch (Exception e) {
            log.warn("Could not fix database columns automatically. Please run the SQL migration scripts manually.");
            log.debug("Error: {}", e.getMessage());
        }
    }
}


