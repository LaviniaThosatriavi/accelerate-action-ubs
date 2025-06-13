package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor  
@AllArgsConstructor
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private User user;
    
    private LocalDate date;
    private Integer dailyPoints;
    private Integer weeklyPoints;
    private Integer monthlyPoints;
    private Integer totalPoints;
    private Integer dailyRank;
    private Integer weeklyRank;
    private Integer monthlyRank;
}