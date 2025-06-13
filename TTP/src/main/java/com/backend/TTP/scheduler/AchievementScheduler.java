package com.backend.TTP.scheduler;

import com.backend.TTP.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AchievementScheduler {
    
    @Autowired
    private AchievementService achievementService;
    
    /**
     * Update leaderboard daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void updateDailyLeaderboard() {
        try {
            achievementService.updateDailyLeaderboard();
            System.out.println("Daily leaderboard updated successfully");
        } catch (Exception e) {
            System.err.println("Failed to update daily leaderboard: " + e.getMessage());
        }
    }
    
    /**
     * Calculate and award leaderboard bonuses daily at 11:59 PM
     */
    @Scheduled(cron = "0 59 23 * * *") // Run at 11:59 PM every day
    public void awardDailyLeaderboardBonuses() {
        try {
            achievementService.updateDailyLeaderboard();
            System.out.println("Daily leaderboard bonuses awarded");
        } catch (Exception e) {
            System.err.println("Failed to award leaderboard bonuses: " + e.getMessage());
        }
    }
}