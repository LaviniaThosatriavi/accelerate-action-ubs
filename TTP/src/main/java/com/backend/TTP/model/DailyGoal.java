package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    private String title;
    private String description;
    private Double allocatedHours;
    private LocalDate goalDate;
    private Boolean isCompleted = false; 
    private Boolean isRecommended = false;
    private LocalDateTime completedAt;
    
    @ManyToOne
    private EnrolledCourse enrolledCourse;
    
    private String resourceUrl;
    private String resourceType;
}