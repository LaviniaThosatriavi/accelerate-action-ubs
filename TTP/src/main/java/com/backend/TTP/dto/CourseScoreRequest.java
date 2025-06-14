package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseScoreRequest {
    private Long enrolledCourseId;  
    private Integer score;        
    private String completionDate;  

    private Integer maxScore = 100;  // Default to 100 since frontend sends scores out of 100
    private String notes;            // Optional
}