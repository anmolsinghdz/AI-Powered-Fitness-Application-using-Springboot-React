package com.fitness.aiservice.service;

import com.fitness.aiservice.dto.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {

    private final GeminiService geminiService;

    public String generateRecommendation(Activity activity){
        String prompt=createPromptForActivity(activity);

        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response: "+aiResponse);
        return aiResponse;
    }

    private 

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following JSON format
                {
                  "summary": {
                    "fitnessLevel": "BEGINNER | INTERMEDIATE | ADVANCED",
                    "activityAssessment": "string",
                    "overallScore": 0
                  },
                  "performanceAnalysis": {
                    "calorieEfficiency": "string",
                    "enduranceAssessment": "string",
                    "heartRateAssessment": "string",
                    "speedAssessment": "string"
                  },
                  "recommendations": {
                    "workoutSuggestions": [
                      "string"
                    ],
                    "recoverySuggestions": [
                      "string"
                    ],
                    "nutritionSuggestions": [
                      "string"
                    ],
                    "hydrationSuggestions": [
                      "string"
                    ]
                  },
                  "healthInsights": {
                    "strengths": [
                      "string"
                    ],
                    "areasForImprovement": [
                      "string"
                    ],
                    "potentialRisks": [
                      "string"
                    ]
                  },
                  "nextGoals": {
                    "shortTermGoals": [
                      "string"
                    ],
                    "longTermGoals": [
                      "string"
                    ]
                  },
                  "motivationMessage": "string"
                }
                Analyze this activity:
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s
                
                Provide a detailed analysis of focusing on performance, improvements, safety guidelines
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
