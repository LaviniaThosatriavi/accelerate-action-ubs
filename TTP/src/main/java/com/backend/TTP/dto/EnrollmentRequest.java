package com.backend.TTP.dto;

import java.time.LocalDate;

public class EnrollmentRequest {
    private Long courseId;
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