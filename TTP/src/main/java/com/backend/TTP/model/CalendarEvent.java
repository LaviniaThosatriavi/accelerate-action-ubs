package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDate eventDate;
    private String title;
    private String description;
    private String eventType; // "DEADLINE", "MILESTONE", etc.
    
    @ManyToOne
    @JoinColumn(name = "enrolled_course_id")
    private EnrolledCourse enrolledCourse; 
}