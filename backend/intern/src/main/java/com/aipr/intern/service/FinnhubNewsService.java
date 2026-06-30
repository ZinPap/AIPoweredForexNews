package com.aipr.intern.service;

import com.aipr.intern.entity.Article;
import com.aipr.intern.repository.ArticleRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinnhubNewsService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArticleRepo articleRepo;

    public FinnhubNewsService(ArticleRepo articleRepo) {
        this.articleRepo = articleRepo;
    }

    // Map Finnhub categories to your entity categories
    private String mapCategory(String finnhubCategory) {
        if (finnhubCategory == null) {
            return "MARKET";
        }
        return switch (finnhubCategory.toLowerCase()) {
            case "forex" -> "FOREX";
            case "crypto" -> "MARKET";
            case "general" -> "MARKET";
            case "earnings" -> "MARKET";
            case "merger" -> "MARKET";
            default -> "MARKET";
        };
    }

    public List<Article> fetchNews(String category) {
        String url = apiUrl + "?category=" + category + "&token=" + apiKey;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response);

            List<Article> articles = new ArrayList<>();
            int skipped = 0;

            for (JsonNode item : jsonArray) {
                String articleUrl = item.path("url").asText();

                // Skip duplicates
                if (articleRepo.existsByUrl(articleUrl)) {
                    continue;
                }

                // Get the summary/content
                String content = item.path("summary").asText();

                // ✅ Skip if content is empty or too short (less than 50 chars)
                if (content == null || content.trim().length() < 50) {
                    skipped++;
                    continue;
                }

                // ✅ Skip if content is just HTML or gibberish
                if (content.trim().matches("^[\\s\\d\\W]+$")) {
                    skipped++;
                    continue;
                }

                Article article = new Article();
                article.setSource(item.path("source").asText("Finnhub"));
                article.setCategory(mapCategory(item.path("category").asText(category)));
                article.setContentType("NEWS");
                article.setTitle(item.path("headline").asText());
                article.setUrl(articleUrl);
                article.setContent(content);

                long timestamp = item.path("datetime").asLong();
                if (timestamp > 0) {
                    article.setPublishedDate(LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()
                    ));
                } else {
                    article.setPublishedDate(LocalDateTime.now());
                }

                articles.add(article);
            }

            if (skipped > 0) {
                System.out.println("  ⚠️ Skipped " + skipped + " articles (empty or too short)");
            }

            if (!articles.isEmpty()) {
                articleRepo.saveAll(articles);
            }

            return articles;

        } catch (Exception e) {
            System.err.println("❌ Finnhub API Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int fetchAllCategories() {
        String[] categories = {"forex", "general", "crypto"};
        int total = 0;

        for (String category : categories) {
            try {
                List<Article> articles = fetchNews(category);
                total += articles.size();
                System.out.println("-> " + category + ": " + articles.size() + " new articles");
            } catch (Exception e) {
                System.err.println("-> " + category + ": " + e.getMessage());
            }
        }

        return total;
    }
}