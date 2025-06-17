package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(description = "Request to enroll in an external course from platforms like Coursera, Udemy, etc.")
public class ExternalCourseEnrollmentDTO {
    
    @Schema(description = "Name or title of the external course", 
            example = "Complete React Developer Course", 
            required = true,
            maxLength = 200)
    @NotBlank(message = "Course name is required")
    @Size(max = 200, message = "Course name cannot exceed 200 characters")
    private String courseName;
    
    @Schema(description = "Platform where the course is hosted", 
            example = "Udemy",
            required = true,
            allowableValues = {"Coursera", "Udemy", "YouTube", "FreeCodeCamp", "Khan Academy", "Pluralsight", "LinkedIn Learning", "Other"})
    @NotBlank(message = "Platform is required")
    private String platform;
    
    @Schema(description = "URL to access the external course", 
            example = "https://udemy.com/course/react-complete-guide", 
            required = true,
            format = "uri")
    @NotBlank(message = "Course URL is required")
    @Pattern(regexp = "^https?://.*", message = "Course URL must be a valid HTTP/HTTPS URL")
    private String courseUrl;
    
    @Schema(description = "Estimated hours required to complete the course", 
            example = "30",
            minimum = "1",
            maximum = "1000")
    @Min(value = 1, message = "Estimated hours must be at least 1")
    @Max(value = 1000, message = "Estimated hours cannot exceed 1000")
    private Integer estimatedHours;
    
    @Schema(description = "Target date for completing the course", 
            example = "2024-08-15", 
            format = "date")
    @Future(message = "Target completion date must be in the future")
    private LocalDate targetCompletionDate;
    
    // Getters and Setters
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getCourseUrl() {
        return courseUrl;
    }
    
    public void setCourseUrl(String courseUrl) {
        this.courseUrl = courseUrl;
    }
    
    public Integer getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }
    
    public void setTargetCompletionDate(LocalDate targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }
}