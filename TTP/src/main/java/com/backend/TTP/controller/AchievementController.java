package com.backend.TTP.controller;

import com.backend.TTP.dto.*;
import com.backend.TTP.model.User;
import com.backend.TTP.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = {"http://localhost:5173"}, 
            allowCredentials = "true",
            allowedHeaders = {"Authorization", "Content-Type", "Accept"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AchievementController {
    
    @Autowired
    private AchievementService achievementService;
    
    /**
     * Get user's achievement profile
     */
    @GetMapping("/profile")
    public ResponseEntity<AchievementProfileResponse> getAchievementProfile(@AuthenticationPrincipal User user) {
        try {
            AchievementProfileResponse response = achievementService.getAchievementProfile(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all badge levels with requirements
     */
    @GetMapping("/badges")
    public ResponseEntity<BadgeLevelResponse> getBadgeLevels() {
        try {
            BadgeLevelResponse response = achievementService.getBadgeLevels();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user's progress toward next badge level
     */
    @GetMapping("/progress")
    public ResponseEntity<AchievementProfileResponse> getProgress(@AuthenticationPrincipal User user) {
        try {
            AchievementProfileResponse response = achievementService.getAchievementProfile(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Trigger point calculation (called by other services)
     */
    @PostMapping("/calculate-points")
    public ResponseEntity<Void> calculatePoints(@AuthenticationPrincipal User user) {
        try {
            // Record daily login when user activity is detected
            achievementService.recordDailyLogin(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get point earning history
     */
    @GetMapping("/points/history")
    public ResponseEntity<PointHistoryResponse> getPointHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            PointHistoryResponse response = achievementService.getPointHistory(user, startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get leaderboard - SIMPLIFIED FOR DAILY ONLY
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponse> getLeaderboard(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            LeaderboardResponse response = achievementService.getLeaderboard(user, period, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current user's rank
     */
    @GetMapping("/leaderboard/rank")
    public ResponseEntity<LeaderboardUserDTO> getCurrentUserRank(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "daily") String period) {
        try {
            LeaderboardResponse leaderboard = achievementService.getLeaderboard(user, period, 1);
            return ResponseEntity.ok(leaderboard.getCurrentUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Manual trigger for daily leaderboard update (for admin/testing)
     */
    @PostMapping("/admin/update-leaderboard")
    public ResponseEntity<Void> updateLeaderboard() {
        try {
            achievementService.updateDailyLeaderboard();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * FIXED: Simple test endpoint to award test points
     */
    @PostMapping("/admin/test-points")
    public ResponseEntity<String> awardTestPoints(@AuthenticationPrincipal User user) {
        try {
            // Get current total points before
            AchievementProfileResponse profileBefore = achievementService.getAchievementProfile(user);
            Integer beforePoints = profileBefore.getTotalPoints();
            
            // Award test points
            achievementService.awardPoints(user, "TEST", 50, "Test points for development", null);
            achievementService.recordDailyLogin(user); // Also record login
            
            // Get points after
            AchievementProfileResponse profileAfter = achievementService.getAchievementProfile(user);
            Integer afterPoints = profileAfter.getTotalPoints();
            
            return ResponseEntity.ok(String.format(
                "Test completed! User: %s (ID: %d)\n" +
                "Total Points: %d → %d\n" +
                "Leaderboard updated automatically\n" +
                "Frontend should show: %d total points",
                user.getUsername(), user.getId(),
                beforePoints, afterPoints,
                afterPoints
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    /**
     * Handle OPTIONS requests for CORS
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}