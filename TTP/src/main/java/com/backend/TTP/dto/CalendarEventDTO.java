package com.backend.TTP.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CalendarEventDTO {
    private Long id;
    private LocalDate eventDate;
    private String title;
    private String description;
    private String eventType;
    private Long enrolledCourseId;
    private String courseTitle;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getEnrolledCourseId() {
        return enrolledCourseId;
    }

    public void setEnrolledCourseId(Long enrolledCourseId) {
        this.enrolledCourseId = enrolledCourseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}