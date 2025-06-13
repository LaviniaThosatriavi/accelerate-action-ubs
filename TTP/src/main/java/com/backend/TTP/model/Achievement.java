package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String category; // COURSE_COMPLETION, CONSISTENCY, LEADERBOARD, SCORE_BASED
    private Integer requiredPoints;
    private String badgeLevel; // NOVICE, APPRENTICE, SCHOLAR, EXPERT, MASTER
    private String iconUrl;
    private Boolean isActive = true;
}