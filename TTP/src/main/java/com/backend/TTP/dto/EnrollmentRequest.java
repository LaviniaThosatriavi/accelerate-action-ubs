package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request to enroll in a course from the internal catalog")
public class EnrollmentRequest {
    
    @Schema(description = "ID of the course to enroll in", example = "101", required = true)
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @Schema(description = "Target date for completing the course", example = "2024-08-15", format = "date")
    @Future(message = "Target completion date must be in the future")
    private LocalDate targetCompletionDate;
    
    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }
    
    public void setTargetCompletionDate(LocalDate targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }
}