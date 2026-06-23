package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.dto.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt=createPromptForActivity(activity);

        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response: "+aiResponse);

        return processAiResponse(activity,aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode=rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContext=textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();

            log.info("PARSED RESPONSE FROM AI: {}", jsonContext);

            JsonNode analysisJson=mapper.readTree(jsonContext);
            JsonNode summaryNode = analysisJson.path("overallAssessment");
            StringBuilder fullSummary=new StringBuilder();

            addAnalysisSection(fullSummary, summaryNode, "fitnessLevel", "FitnessLevel:");
            addAnalysisSection(fullSummary, summaryNode, "activityRating", "ActivityRating:");
            addAnalysisSection(fullSummary, summaryNode, "summary", "Summary:");

            List<String> improvements=extractRecommendations(analysisJson.path("performanceImprovements"));

            List<String> suggestions=extractSuggestions(analysisJson.path("suggestions"));

            List<String> safety=extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullSummary.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createDefaultRecommendation(activity);
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness expert"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay Hydrated",
                        "Listen to your Body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety=new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty()?
                Collections.singletonList("Follow general guidelines"):
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions=new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout=suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s %s", workout, description));
            });
        }
        return suggestions.isEmpty()?
                Collections.singletonList("No specific suggestions provided"):
                suggestions;
    }

    private List<String> extractRecommendations(JsonNode improvementsNode) {
        List<String> improvements=new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area=improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s %s", area, detail));
            });
        }

        return improvements.isEmpty()?
                Collections.singletonList("No specific recommendation provided"):
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                        Analyze the following fitness activity and act as a professional fitness coach.
                        
                        Generate ONLY valid JSON in the exact format below. Do not include markdown, explanations, or additional text outside the JSON.
                        
                        {
                                  "overallAssessment": {
                                    "fitnessLevel": "BEGINNER | INTERMEDIATE | ADVANCED",
                                    "activityRating": 0,
                                    "summary": "string"
                                  },
                                  "performanceImprovements": [
                                    {
                                      "area": "string",
                                      "recommendation": "string"
                                    }
                                  ],
                                  "suggestions": [
                                    {
                                      "workout": "string",
                                      "description": "string"
                                    }
                                  ],
                                  "safety": [
                                    "string"
                                  ]
                                }
                        
                                Requirements:
                                - Provide at least 3 performance improvements.
                                - Provide at least 3 workout suggestions.
                                - Provide at least 4 safety recommendations.
                                - Focus on actionable fitness advice.
                                - Return ONLY valid JSON.
                                                
                        Activity Details:
                        
                        Activity Type: %s
                        Duration: %d minutes
                        Calories Burned: %d
                        Additional Metrics: %s
                        
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
