package com.fitness.ai_service.application.services;

import com.fitness.ai_service.application.dto.GeminiGenerationResult;
import com.fitness.ai_service.application.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;

    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${gemini.api.url:}") String apiUrl,
                         @Value("${gemini.api.key:}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.apiUrl = validateApiUrl(apiUrl);
        this.apiKey = apiKey == null ? "" : apiKey.strip();
    }

    public GeminiGenerationResult getGeminiResponse(String question) {
        if (apiUrl.isBlank() || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_URL and GEMINI_API_KEY must be configured");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be blank");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", question)))
                )
        );

        GeminiResponse response = webClient.post()
                .uri(apiUrl)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block(Duration.ofSeconds(30));

        if (response != null && response.candidates() != null) {
            for (GeminiResponse.Candidate candidate : response.candidates()) {
                if (candidate == null || candidate.content() == null || candidate.content().parts() == null) {
                    continue;
                }
                String text = candidate.content().parts().stream()
                        .filter(Objects::nonNull)
                        .map(GeminiResponse.Part::text)
                        .filter(part -> part != null && !part.isBlank())
                        .collect(Collectors.joining("\n"));
                if (!text.isBlank()) {
                    Integer totalTokenCount = response.usageMetadata() == null
                            ? null : response.usageMetadata().totalTokenCount();
                    return new GeminiGenerationResult(text, totalTokenCount);
                }
            }
        }

        String blockReason = response == null || response.promptFeedback() == null
                ? null : response.promptFeedback().blockReason();
        throw new IllegalStateException("Gemini returned no text"
                + (blockReason == null ? "" : " (block reason: " + blockReason + ")"));
    }

    private static String validateApiUrl(String configuredUrl) {
        String url = configuredUrl == null ? "" : configuredUrl.strip();
        if (url.isEmpty()) {
            return url;
        }

        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            String path = uri.getPath();
            boolean geminiEndpoint = "https".equalsIgnoreCase(uri.getScheme())
                    && "generativelanguage.googleapis.com".equalsIgnoreCase(host)
                    && path != null && path.matches("/v1(?:beta)?/models/[^/]+:generateContent");
            boolean localTestEndpoint = "http".equalsIgnoreCase(uri.getScheme())
                    && ("127.0.0.1".equals(host) || "localhost".equalsIgnoreCase(host))
                    && path != null && path.endsWith("/generateContent");
            if ((geminiEndpoint || localTestEndpoint) && uri.getRawQuery() == null
                    && uri.getUserInfo() == null && uri.getFragment() == null) {
                return url;
            }
        } catch (IllegalArgumentException ignored) {
        }

        throw new IllegalStateException("GEMINI_API_URL must be a direct Gemini generateContent endpoint");
    }
}
