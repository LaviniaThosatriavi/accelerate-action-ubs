package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Calendar event information representing deadlines, milestones, or important dates")
public class CalendarEventDTO {
    
    @Schema(description = "Unique event identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Date when the event occurs", example = "2024-06-25", format = "date")
    private LocalDate eventDate;
    
    @Schema(description = "Event title or name", example = "React Course Assignment Due", maxLength = 255)
    private String title;
    
    @Schema(description = "Detailed description of the event", example = "Final project submission for React fundamentals course")
    private String description;
    
    @Schema(description = "Type of calendar event", 
            example = "DEADLINE",
            allowableValues = {"DEADLINE", "MILESTONE", "REMINDER", "ASSESSMENT", "EXAM"})
    private String eventType;
    
    @Schema(description = "ID of the associated enrolled course (if applicable)", example = "123")
    private Long enrolledCourseId;
    
    @Schema(description = "Title of the associated course", example = "React Fundamentals", accessMode = Schema.AccessMode.READ_ONLY)
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