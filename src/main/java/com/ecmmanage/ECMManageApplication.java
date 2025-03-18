// This package declaration tells Java where this file is located in the project structure.
package com.ecmmanage;

// Importing Spring Boot's core libraries.
import org.springframework.boot.SpringApplication; // This helps in running the application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration.

/**
 * This is the main entry point of our ECM (Enterprise Content Management) system.
 * It will launch the Spring Boot framework and start our application.
 */
@SpringBootApplication(scanBasePackages = "com.ecmmanage") // This annotation tells Spring Boot to configure itself automatically.
public class ECMManageApplication {

    // This is the main method. It is the first method executed when the program runs.
    public static void main(String[] args) {
        // SpringApplication.run() starts the Spring Boot application.
        SpringApplication.run(ECMManageApplication.class, args);
    }
}
