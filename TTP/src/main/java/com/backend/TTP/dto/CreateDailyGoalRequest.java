package com.backend.TTP.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDailyGoalRequest {
    private String title;
    private String description;
    private Double allocatedHours;
    private LocalDate goalDate;
    private Long enrolledCourseId;
    private String resourceUrl;
    private String resourceType; 
}