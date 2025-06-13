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
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    private String activityType; // COURSE_COMPLETION, DAILY_LOGIN, LEADERBOARD_BONUS, SCORE_BONUS
    private Integer pointsEarned;
    private LocalDateTime earnedAt;
    private String description;
    private Long relatedEntityId; // courseId, goalId, etc.
}