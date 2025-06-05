package com.backend.TTP.dto;

import java.time.LocalDate;

public class EnrolledCourseDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String platform;
    private String url;
    private LocalDate enrollmentDate;
    private LocalDate targetCompletionDate;
    private LocalDate actualCompletionDate;
    private Integer progressPercentage;
    private String status;
    private Integer hoursSpent;
    private Integer estimatedHours;
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