package com.aipr.intern.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class EnvLoader {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("GROQ_API_KEY");

            if (apiKey != null && !apiKey.isEmpty()) {
                System.setProperty("GROQ_API_KEY", apiKey);
                System.out.println("GROQ_API_KEY loaded from .env");
            } else {
                System.out.println("GROQ_API_KEY not found in .env");
            }
        } catch (Exception e) {
            System.out.println(".env file not found, using system environment variables");
        }
    }
}