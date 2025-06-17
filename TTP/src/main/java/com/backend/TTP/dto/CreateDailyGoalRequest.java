package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Schema(description = "Request to create a new daily goal")
public class CreateDailyGoalRequest {
    
    @Schema(description = "Title of the daily goal", 
            example = "Complete React Hooks Tutorial", 
            required = true,
            maxLength = 100)
    @NotBlank(message = "Goal title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    @Schema(description = "Detailed description of the goal", 
            example = "Watch and practice React Hooks tutorial from section 5",
            maxLength = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Schema(description = "Number of hours allocated to complete this goal", 
            example = "2.5",
            minimum = "0.1",
            maximum = "24.0")
    @DecimalMin(value = "0.1", message = "Allocated hours must be at least 0.1")
    @DecimalMax(value = "24.0", message = "Allocated hours cannot exceed 24 hours per day")
    private Double allocatedHours;
    
    @Schema(description = "Target date to complete the goal", 
            example = "2024-06-25",
            format = "date")
    @Future(message = "Goal date must be in the future")
    private LocalDate goalDate;
    
    @Schema(description = "ID of the enrolled course this goal is related to (optional)", 
            example = "123")
    private Long enrolledCourseId;
    
    @Schema(description = "URL to learning resource for this goal", 
            example = "https://youtube.com/watch?v=abc123",
            format = "uri")
    private String resourceUrl;
    
    @Schema(description = "Type of learning resource", 
            example = "VIDEO",
            allowableValues = {"VIDEO", "ARTICLE", "DOCUMENTATION", "EXERCISE", "PROJECT", "BOOK", "COURSE"})
    private String resourceType;
}