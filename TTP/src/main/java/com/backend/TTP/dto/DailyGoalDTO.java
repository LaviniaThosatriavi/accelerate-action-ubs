package com.backend.TTP.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyGoalDTO {
    private Long id;
    private String title;
    private String description;
    private Double allocatedHours;
    private LocalDate goalDate;
    private Boolean isCompleted;
    private Boolean isRecommended;
    private LocalDateTime completedAt;
    private Long enrolledCourseId; 
    private String courseName; 
    private String resourceUrl;
    private String resourceType;
}