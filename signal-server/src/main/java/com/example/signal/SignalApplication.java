package com.example.signal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot application. This class uses the
 * {@code @SpringBootApplication} annotation to enable component scanning,
 * auto-configuration and to declare that this is a Boot application. The
 * {@code main} method delegates to {@link SpringApplication#run} to launch
 * the embedded web server and initialize the application context.
 */
@SpringBootApplication
public class SignalApplication {
    public static void main(String[] args) {
        SpringApplication.run(SignalApplication.class, args);
    }
}