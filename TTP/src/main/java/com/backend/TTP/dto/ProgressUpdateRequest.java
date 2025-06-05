package com.backend.TTP.dto;

public class ProgressUpdateRequest {
    private Long enrolledCourseId;
    private Integer progressPercentage;
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