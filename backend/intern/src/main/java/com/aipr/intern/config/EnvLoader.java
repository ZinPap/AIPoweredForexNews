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
            String groqKey = dotenv.get("GROQ_API_KEY");
            String finnhubKey = dotenv.get("FINNHUB_API_KEY");

            if (groqKey != null && !groqKey.isEmpty()) {
                System.setProperty("GROQ_API_KEY", groqKey);
                System.out.println("GROQ_API_KEY loaded from .env");
            }

            if (finnhubKey != null && !finnhubKey.isEmpty()) {
                System.setProperty("FINNHUB_API_KEY", finnhubKey);
                System.out.println("FINNHUB_API_KEY loaded from .env");
            }
        } catch (Exception e) {
            System.out.println(".env file not found, using system environment variables");
        }
    }
}