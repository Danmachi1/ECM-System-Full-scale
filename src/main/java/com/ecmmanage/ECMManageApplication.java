// This package declaration tells Java where this file is located in the project structure.
package com.ecmmanage;

// Importing Spring Boot's core libraries.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication; // Helps in running the application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration.
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * This is the main entry point of our ECM (Enterprise Content Management) system.
 * It will launch the Spring Boot framework and start our application.
 */
@SpringBootApplication(scanBasePackages = "com.ecmmanage") // Ensures Spring scans this package for components.
@EnableMethodSecurity
public class ECMManageApplication {

    // Logger instance for better application monitoring.
    private static final Logger logger = LoggerFactory.getLogger(ECMManageApplication.class);

    // This is the main method. It is the first method executed when the program runs.
    public static void main(String[] args) {
        logger.info("Starting ECMManage Application...");
        
        // SpringApplication.run() starts the Spring Boot application.
        SpringApplication.run(ECMManageApplication.class, args);

        logger.info("ECMManage Application started successfully.");
    }
}
