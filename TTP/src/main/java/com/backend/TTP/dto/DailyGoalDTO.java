package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Daily goal information with completion status and progress tracking")
public class DailyGoalDTO {
    
    @Schema(description = "Unique goal identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Title of the daily goal", example = "Complete React Hooks Tutorial", maxLength = 100)
    private String title;
    
    @Schema(description = "Detailed description of the goal", example = "Watch and practice React Hooks tutorial from section 5")
    private String description;
    
    @Schema(description = "Number of hours allocated to complete this goal", example = "2.5", minimum = "0")
    private Double allocatedHours;
    
    @Schema(description = "Target date to complete the goal", example = "2024-06-25", format = "date")
    private LocalDate goalDate;
    
    @Schema(description = "Whether the goal has been completed", example = "false", defaultValue = "false")
    private Boolean isCompleted;
    
    @Schema(description = "Whether this goal was system-recommended", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isRecommended;
    
    @Schema(description = "Timestamp when the goal was completed", example = "2024-06-25T14:30:00", format = "date-time", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime completedAt;
    
    @Schema(description = "ID of the enrolled course this goal is related to", example = "123")
    private Long enrolledCourseId;
    
    @Schema(description = "Name of the associated course", example = "React Fundamentals", accessMode = Schema.AccessMode.READ_ONLY)
    private String courseName;
    
    @Schema(description = "URL to learning resource for this goal", example = "https://youtube.com/watch?v=abc123", format = "uri")
    private String resourceUrl;
    
    @Schema(description = "Type of learning resource", 
            example = "VIDEO",
            allowableValues = {"VIDEO", "ARTICLE", "DOCUMENTATION", "EXERCISE", "PROJECT", "BOOK", "COURSE"})
    private String resourceType;
}