package com.fitness.ai_service.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitness.ai_service.application.dto.GeminiGenerationResult;
import com.fitness.ai_service.application.dto.GeneratedRecommendationResponse;
import com.fitness.ai_service.domain.models.Activity;
import com.fitness.ai_service.domain.models.Recommendation;
import com.fitness.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public ApiResponse<GeneratedRecommendationResponse> generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        GeminiGenerationResult response = geminiService.getGeminiResponse(prompt);
        log.info("Gemini response for activity {}: {}", activity.getId(), response == null ? "null" : response.text());
        return processAiResponse(activity, response);
    }

    private ApiResponse<GeneratedRecommendationResponse> processAiResponse(
            Activity activity, GeminiGenerationResult response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonContent = response == null || response.text() == null ? "" : response.text().strip();
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceFirst("(?i)^```(?:json)?\\s*", "")
                        .replaceFirst("\\s*```$", "")
                        .strip();
            }
            JsonNode recommendation = objectMapper.readTree(jsonContent);
            if (recommendation == null || !recommendation.isObject()) {
                throw new IllegalArgumentException("Gemini recommendation must be a JSON object");
            }

            JsonNode analysisNode = recommendation.path("analysis");
            if (!analysisNode.isObject()) {
                throw new IllegalArgumentException("Gemini recommendation must contain an analysis object");
            }
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall: ");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace: ");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate: ");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned: ");

            if (fullAnalysis.isEmpty()) {
                throw new IllegalArgumentException("Gemini recommendation analysis must not be empty");
            }

            List<String> improvements = extractImprovements(recommendation.path("improvements"));
            List<String> suggestions = extractSuggestions(recommendation.path("suggestions"));
            List<String> safety = extractSafetyTips(recommendation.path("safety"));

            Recommendation result = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

            return ApiResponse.success("Recommendation generated successfully",
                    new GeneratedRecommendationResponse(
                            result.getActivityId(), result.getUserId(), result.getActivityType(),
                            result.getRecommendation(), result.getImprovements(), result.getSuggestions(),
                            result.getSafety(), result.getCreatedAt(), response.totalTokenCount()));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private ApiResponse<GeneratedRecommendationResponse> createDefaultRecommendation(Activity activity) {
        Recommendation defaultRecommendation = Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("No specific recommendations available.")
                .improvements(Collections.singletonList("Continue with your current routine."))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional."))
                .safety(Collections.singletonList("Always warm up before exercising and stay hydrated."))
                .createdAt(LocalDateTime.now())
                .build();

        return ApiResponse.success("Default recommendation generated due to processing error",
                new GeneratedRecommendationResponse(
                        defaultRecommendation.getActivityId(),
                        defaultRecommendation.getUserId(),
                        defaultRecommendation.getActivityType(),
                        defaultRecommendation.getRecommendation(),
                        defaultRecommendation.getImprovements(),
                        defaultRecommendation.getSuggestions(),
                        defaultRecommendation.getSafety(),
                        defaultRecommendation.getCreatedAt(),
                        0));
    }

    private List<String> extractSafetyTips(JsonNode safetyNode) {
        List<String> safetyList = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> {
                safetyList.add(item.asText());
            });

        }
        return safetyList.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines.") : safetyList;
    }

    private List<String> extractSuggestions(JsonNode suggestions) {
        List<String> suggestionList = new ArrayList<>();
        if (suggestions != null && suggestions.isArray()) {
            suggestions.forEach(suggestionNode -> {
                String workout = suggestionNode.path("workout").asText();
                String description = suggestionNode.path("description").asText();
                suggestionList.add(String.format("Workout: %s, Description: %s", workout, description));
            });
        }
        return suggestionList.isEmpty() ?
                Collections.singletonList("No specific suggestions provided.") : suggestionList;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvementList = new ArrayList<>();
        if (improvementsNode != null && improvementsNode.isArray()) {
            improvementsNode.forEach(improvementNode -> {
                String area = improvementNode.path("area").asText();
                String detail = improvementNode.path("recommendation").asText(null);
                improvementList.add(String.format("Area: %s, Recommendation: %s", area, detail));
            });
            }
        return improvementList.isEmpty() ?
                Collections.singletonList("No specific improvements provided.") : improvementList;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        String value = analysisNode.path(key).asText("");
        if (!value.isBlank()) {
            fullAnalysis.append(prefix)
                    .append(value)
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null");
        }

        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following JSON format:
                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "suggestions": [
                    {
                      "workout": "Workout name",
                      "description": "Detailed workout description"
                    }
                  ],
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }

                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s

                Provide detailed analysis focusing on performance, improvements, next workouts, and safety.
                Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
