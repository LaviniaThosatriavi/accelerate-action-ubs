package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Enrolled course information with progress tracking and completion details")
public class EnrolledCourseDTO {
    
    @Schema(description = "Unique enrollment identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "ID of the course from course catalog", example = "101")
    private Long courseId;
    
    @Schema(description = "Title of the enrolled course", example = "React Fundamentals")
    private String courseTitle;
    
    @Schema(description = "Learning platform where the course is hosted", 
            example = "Coursera",
            allowableValues = {"Coursera", "Udemy", "YouTube", "FreeCodeCamp", "Khan Academy", "Internal", "Other"})
    private String platform;
    
    @Schema(description = "URL to access the course", example = "https://coursera.org/learn/react-fundamentals", format = "uri")
    private String url;
    
    @Schema(description = "Date when the user enrolled in the course", example = "2024-06-01", format = "date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate enrollmentDate;
    
    @Schema(description = "Target date for course completion", example = "2024-08-15", format = "date")
    private LocalDate targetCompletionDate;
    
    @Schema(description = "Actual date when the course was completed", example = "2024-08-10", format = "date", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate actualCompletionDate;
    
    @Schema(description = "Current progress percentage (0-100)", example = "75", minimum = "0", maximum = "100")
    private Integer progressPercentage;
    
    @Schema(description = "Current enrollment status", 
            example = "IN_PROGRESS",
            allowableValues = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED", "PAUSED", "DROPPED"})
    private String status;
    
    @Schema(description = "Total hours spent on this course", example = "25", minimum = "0")
    private Integer hoursSpent;
    
    @Schema(description = "Estimated total hours required to complete the course", example = "40", minimum = "1")
    private Integer estimatedHours;
    
    @Schema(description = "Course category or subject area", 
            example = "programming",
            allowableValues = {"programming", "design", "business", "data-science", "marketing", "photography", "other"})
    private String category;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseTitle() {
        return courseTitle;
    }
    
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }
    
    public void setTargetCompletionDate(LocalDate targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }
    
    public LocalDate getActualCompletionDate() {
        return actualCompletionDate;
    }
    
    public void setActualCompletionDate(LocalDate actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }
    
    public Integer getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getHoursSpent() {
        return hoursSpent;
    }
    
    public void setHoursSpent(Integer hoursSpent) {
        this.hoursSpent = hoursSpent;
    }
    
    public Integer getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}