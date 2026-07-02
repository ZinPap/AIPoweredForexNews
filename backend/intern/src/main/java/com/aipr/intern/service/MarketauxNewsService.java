package com.aipr.intern.service;

import com.aipr.intern.entity.Article;
import com.aipr.intern.repository.ArticleRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
public class MarketauxNewsService {

        // Injects the API key from application.yaml (which it gets from .env)
        @Value("${marketaux.api.key}")
        private String apiKey;

        //Tools
        private final RestTemplate restTemplate = new RestTemplate();
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final ArticleRepo articleRepo;

        public MarketauxNewsService(ArticleRepo articleRepo) {
            this.articleRepo = articleRepo;
        }

        public int fetchNews(){
            try{
                //rest call url limiting to 20 articles per call due to massive article data offered
                String url = "https://api.marketaux.com/v1/news/all?symbols=TSLA&language=en&api_token=" + apiKey + "&limit=20";

                //rest call response as String
                String response = restTemplate.getForObject(url, String.class);

                //making the response json
                JsonNode json = objectMapper.readTree(response);

                //going to where the articles are stored
                JsonNode articles = json.path("data");

                int saved=0;

                //time to loop through articles and save them in the table
                for (JsonNode item : articles) {
                    //finding article urls
                    String aurl=item.path("url").asText();

                    //checking if it already exists
                    if(articleRepo.existsByUrl(aurl)){
                        continue;
                    }

                    //getting content
                    String content=item.path("snippet").asText();

                    //if content is empty we don't want it
                    if(content==null || content.isEmpty()){
                        continue;
                    }

                    Article article = new Article();
                    article.setSource(item.path("source").path("domain").asText("Marketaux"));
                    article.setCategory(detectCategory(content));

                    article.setContentType("NEWS");
                    article.setTitle(item.path("title").asText());
                    article.setUrl(aurl);
                    article.setContent(content);
                    article.setPublishedDate(LocalDateTime.now());

                    articleRepo.save(article);
                    saved++;
                }
                System.out.println("Marketaux: " + saved + " new articles");
                return saved;
            }
            catch (Exception e){
                System.err.println("Marketaux Error: " + e.getMessage());
                return 0;
            }
        }

    private String detectCategory(String content) {
        String text = content.toLowerCase();

        // Check for REGULATORY keywords
        if (text.contains("fed") || text.contains("federal reserve") ||
                text.contains("ecb") || text.contains("european central bank") ||
                text.contains("central bank") || text.contains("regulation") ||
                text.contains("regulatory") || text.contains("sec") ||
                text.contains("securities and exchange") || text.contains("compliance") ||
                text.contains("treasury") || text.contains("fiscal") ||
                text.contains("monetary policy") || text.contains("interest rate") ||
                text.contains("bank of england") || text.contains("boj") ||
                text.contains("bank of japan")) {
            return "REGULATORY";
        }

        // Check for FOREX keywords
        if (text.contains("eur") || text.contains("usd") || text.contains("gbp") ||
                text.contains("jpy") || text.contains("chf") || text.contains("cad") ||
                text.contains("aud") || text.contains("nzd") || text.contains("forex") ||
                text.contains("currency") || text.contains("exchange rate") ||
                text.contains("fx") || text.contains("euro") || text.contains("dollar") ||
                text.contains("pound") || text.contains("yen") || text.contains("swiss franc") ||
                text.contains("currency pair") || text.contains("us dollar") ||
                text.contains("foreign exchange")) {
            return "FOREX";
        }

        // Default to MARKET
        return "MARKET";
    }
}
