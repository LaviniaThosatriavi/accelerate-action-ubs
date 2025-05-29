package com.backend.TTP.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class LearningPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;
    
    @Lob
    @Column(columnDefinition = "TEXT") // Add for better DB compatibility
    private String pathContent;
    private LocalDate createdDate;
}