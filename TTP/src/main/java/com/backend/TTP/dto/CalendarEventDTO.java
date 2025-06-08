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
}