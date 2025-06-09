package com.backend.TTP.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class EnrolledCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Course course;
    
    private LocalDate enrollmentDate;
    private LocalDate targetCompletionDate;
    private LocalDate actualCompletionDate;
    private Integer progressPercentage = 0;
    private String status = "NOT_STARTED"; // NOT_STARTED, IN_PROGRESS, COMPLETED
    private Integer hoursSpent = 0;
    private Integer hoursSpentThisWeek = 0; // New field to track weekly hours
    private LocalDate weekStartDate; // To track which week the hoursSpentThisWeek refers to
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
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
    
    public Integer getHoursSpentThisWeek() {
        return hoursSpentThisWeek;
    }
    
    public void setHoursSpentThisWeek(Integer hoursSpentThisWeek) {
        this.hoursSpentThisWeek = hoursSpentThisWeek;
    }
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
}