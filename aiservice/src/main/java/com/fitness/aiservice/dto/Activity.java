package com.fitness.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private String id;
    private String userId;

    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private String type;

    private Map<String, Object> additionalMetrics;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
