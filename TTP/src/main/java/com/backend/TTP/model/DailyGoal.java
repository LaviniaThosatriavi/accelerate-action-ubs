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
    @JoinColumn(name = "user_id")
    private User user;
    
    private String title;
    private String description;
    private Double allocatedHours;
    private LocalDate goalDate;
    private Boolean isCompleted = false;
    private Boolean isRecommended = false; // True if system-generated, false if user-added
    private LocalDateTime completedAt;
    
    @ManyToOne
    @JoinColumn(name = "enrolled_course_id")
    private EnrolledCourse enrolledCourse; // Optional - if goal is related to a course
    
    private String resourceUrl; // YouTube URL, article URL, etc.
    private String resourceType; // "VIDEO", "COURSE", "ARTICLE", etc.
}