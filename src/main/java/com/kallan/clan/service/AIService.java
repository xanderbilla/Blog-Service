package com.kallan.clan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kallan.clan.exception.AIServiceException;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateSummary(String text) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
                + geminiApiKey;

        HttpHeaders headers = createHeaders();
        Map<String, Object> requestBody = createRequestBody(text);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        return extractSummaryFromResponse(response);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private Map<String, Object> createRequestBody(String text) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", "Summarize this: " + text)))),
                "generationConfig", Map.of("maxOutputTokens", 100));
    }

    private String extractSummaryFromResponse(ResponseEntity<Map<String, Object>> response) {
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("candidates")) {
            Object candidatesObj = responseBody.get("candidates");
            if (candidatesObj instanceof List<?> candidates && !candidates.isEmpty()) {
                Object firstCandidateObj = candidates.get(0);
                if (firstCandidateObj instanceof Map<?, ?> firstCandidate) {
                    Object contentObj = firstCandidate.get("content");
                    if (contentObj instanceof Map<?, ?> content) {
                        Object partsObj = content.get("parts");
                        if (partsObj instanceof List<?> parts && !parts.isEmpty()) {
                            Object firstPartObj = parts.get(0);
                            if (firstPartObj instanceof Map<?, ?> firstPart && firstPart.containsKey("text")) {
                                return (String) firstPart.get("text");
                            }
                        }
                    }
                }
            }
            throw new AIServiceException("Could not extract text from response");
        } else {
            throw new AIServiceException("Response body is null or missing expected data");
        }
    }
}