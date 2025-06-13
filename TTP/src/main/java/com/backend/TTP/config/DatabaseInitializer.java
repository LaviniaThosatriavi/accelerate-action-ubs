package com.backend.TTP.config;

import com.backend.TTP.model.Achievement;
import com.backend.TTP.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner, Ordered {
    
    @Autowired
    private AchievementRepository achievementRepository;
    
    @Override
    public int getOrder() {
        return 2; // Run after DatabaseFixer (which is 1)
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no achievements exist
        if (achievementRepository.count() == 0) {
            System.out.println("Initializing achievement data...");
            initializeAchievements();
            System.out.println("Achievement data initialized successfully!");
        } else {
            System.out.println("Achievement data already exists, skipping initialization.");
        }
    }
    
    private void initializeAchievements() {
        Achievement[] achievements = {
            new Achievement(null, "First Steps", "Complete your first course", "COURSE_COMPLETION", 0, "NOVICE", "/badges/first-course.png", true),
            new Achievement(null, "Learning Explorer", "Reach Novice level", "BADGE_LEVEL", 0, "NOVICE", "/badges/novice.png", true),
            new Achievement(null, "Knowledge Seeker", "Reach Apprentice level", "BADGE_LEVEL", 100, "APPRENTICE", "/badges/apprentice.png", true),
            new Achievement(null, "Dedicated Learner", "Reach Scholar level", "BADGE_LEVEL", 300, "SCHOLAR", "/badges/scholar.png", true),
            new Achievement(null, "Skill Master", "Reach Expert level", "BADGE_LEVEL", 700, "EXPERT", "/badges/expert.png", true),
            new Achievement(null, "Learning Champion", "Reach Master level", "BADGE_LEVEL", 1500, "MASTER", "/badges/master.png", true),
            new Achievement(null, "Consistent Learner", "Maintain 7-day login streak", "CONSISTENCY", 0, "APPRENTICE", "/badges/streak-7.png", true),
            new Achievement(null, "Dedicated Student", "Maintain 30-day login streak", "CONSISTENCY", 0, "SCHOLAR", "/badges/streak-30.png", true),
            new Achievement(null, "High Achiever", "Score 90%+ on 5 courses", "SCORE_BASED", 0, "EXPERT", "/badges/high-scorer.png", true),
            new Achievement(null, "Course Collector", "Complete 10 courses", "COURSE_COMPLETION", 0, "SCHOLAR", "/badges/course-collector.png", true)
        };
        
        for (Achievement achievement : achievements) {
            achievementRepository.save(achievement);
        }
    }
}