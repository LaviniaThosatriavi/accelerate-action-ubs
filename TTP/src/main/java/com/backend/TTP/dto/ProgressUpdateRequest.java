package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update progress for an enrolled course")
public class ProgressUpdateRequest {
    
    @Schema(description = "ID of the enrolled course to update", 
            example = "123", 
            required = true)
    @NotNull(message = "Enrolled course ID is required")
    private Long enrolledCourseId;
    
    @Schema(description = "Updated progress percentage (0-100)", 
            example = "75", 
            required = true,
            minimum = "0", 
            maximum = "100")
    @NotNull(message = "Progress percentage is required")
    @Min(value = 0, message = "Progress percentage cannot be negative")
    @Max(value = 100, message = "Progress percentage cannot exceed 100")
    private Integer progressPercentage;
    
    @Schema(description = "Additional hours spent on the course since last update", 
            example = "3", 
            minimum = "0",
            maximum = "24")
    @Min(value = 0, message = "Additional hours cannot be negative")
    @Max(value = 24, message = "Cannot add more than 24 hours in a single update")
    private Integer additionalHoursSpent;
    
    // Getters and Setters
    public Long getEnrolledCourseId() {
        return enrolledCourseId;
    }
    
    public void setEnrolledCourseId(Long enrolledCourseId) {
        this.enrolledCourseId = enrolledCourseId;
    }
    
    public Integer getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public Integer getAdditionalHoursSpent() {
        return additionalHoursSpent;
    }
    
    public void setAdditionalHoursSpent(Integer additionalHoursSpent) {
        this.additionalHoursSpent = additionalHoursSpent;
    }
}