package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to record a course completion score")
public class CourseScoreRequest {
    
    @Schema(description = "ID of the enrolled course being scored", 
            example = "123", 
            required = true)
    @NotNull(message = "Enrolled course ID is required")
    private Long enrolledCourseId;
    
    @Schema(description = "Score achieved in the course", 
            example = "85", 
            required = true,
            minimum = "0", 
            maximum = "100")
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Integer score;
    
    @Schema(description = "Date when the course was completed", 
            example = "2024-06-15",
            format = "date",
            pattern = "YYYY-MM-DD")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in YYYY-MM-DD format")
    private String completionDate;
    
    @Schema(description = "Maximum possible score for the course", 
            example = "100",
            defaultValue = "100",
            minimum = "1")
    @Min(value = 1, message = "Max score must be at least 1")
    private Integer maxScore = 100;  // Default to 100 since frontend sends scores out of 100
    
    @Schema(description = "Optional notes or comments about the course completion", 
            example = "Excellent course, learned a lot about React hooks",
            maxLength = 500)
    private String notes;            // Optional
}