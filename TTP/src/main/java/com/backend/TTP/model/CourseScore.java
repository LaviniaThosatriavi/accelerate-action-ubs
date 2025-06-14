package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "course_id")
    private Long courseId;
    
    @ManyToOne
    @JoinColumn(name = "enrolled_course_id")
    private EnrolledCourse enrolledCourse;
    
    private Integer score;
    private Integer maxScore;
    private Double percentage;
    private LocalDateTime completionDate;
    private String notes;
    private Integer pointsEarned;
    
    @PrePersist
    @PreUpdate
    private void calculatePercentage() {
        if (score != null && maxScore != null && maxScore > 0) {
            this.percentage = (score.doubleValue() / maxScore.doubleValue()) * 100;
        }
    }
}