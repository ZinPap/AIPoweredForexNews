package com.aipr.intern.service;

import com.aipr.intern.dto.ArticleWithStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.*;

import static org.apache.logging.log4j.util.StringBuilders.escapeJson;

@Service
public class AISummaryService {

    //api key from application.yaml
    @Value("${groq.api.key}")
    private String apiKey;

    //api url from application.yaml
    @Value("${groq.api.url}")
    private String apiUrl;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    //method to build a prompt
    private String buildPrompt(String content, String category, String contentType) {
        return String.format("""
            You are a regulatory and financial-markets analyst.
            Analyze the following item. It belongs to the "%s" category
            (REGULATORY, FOREX, or MARKET) and is of type "%s".

            Provide:
            1. Executive Summary (2-3 sentences)
            2. Impact Level (LOW, MEDIUM, HIGH, CRITICAL)
            3. Who Is Affected
            4. Key Topics (array of short tags)

            Return ONLY valid JSON with keys:
            executiveSummary, impactLevel, affectedParties, topics

            Item:
            %s
            """, category, contentType, content);
    }

    //method to call to generate summary
    public ArticleWithStatusDto generateSummary(
            String category,
            String content,
            String contentType
    ) {
        try {
            String prompt = buildPrompt(content, category, contentType);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "You are a regulatory and financial-markets analyst. Return only valid JSON."));
            messages.add(Map.of("role", "user", "content", prompt));

            Map<String, Object> requestBodyMap = new LinkedHashMap<>();
            requestBodyMap.put("model", "llama-3.1-8b-instant");
            requestBodyMap.put("messages", messages);
            requestBodyMap.put("temperature", 0.3);

            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            if (response != null && response.trim().startsWith("`")) {
                response = response.trim()
                        .replaceAll("^```json\\s*", "")
                        .replaceAll("^```\\s*", "")
                        .replaceAll("\\s*```$", "")
                        .trim();
            }

            var json = objectMapper.readTree(response);

            if (json.has("error")) {
                String errorMsg = json.path("error").path("message").asText();
                throw new RuntimeException("Groq API Error: " + errorMsg);
            }

            String aiContent = json.path("choices").get(0).path("message").path("content").asText();

            // Convert array fields to comma-separated strings
            ObjectMapper mapper = new ObjectMapper();
            JsonNode contentNode = mapper.readTree(aiContent);

            if (contentNode.has("affectedParties") && contentNode.get("affectedParties").isArray()) {
                List<String> parties = new ArrayList<>();
                contentNode.get("affectedParties").forEach(node -> parties.add(node.asText()));
                ((ObjectNode) contentNode).put("affectedParties", String.join(", ", parties));
            }

            if (contentNode.has("topics") && contentNode.get("topics").isArray()) {
                List<String> topicList = new ArrayList<>();
                contentNode.get("topics").forEach(node -> topicList.add(node.asText()));
                ((ObjectNode) contentNode).put("topics", String.join(", ", topicList));
            }

            return mapper.readValue(contentNode.toString(), ArticleWithStatusDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

}


