package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User achievement profile response containing points, badges, and progress information")
public class AchievementProfileResponse {
    
    @Schema(description = "Unique user identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    
    @Schema(description = "Username of the user", example = "john_doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    
    @Schema(description = "Total points earned by the user", example = "1250", minimum = "0")
    private Integer totalPoints;
    
    @Schema(description = "Current badge level of the user", example = "BRONZE", 
            allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"})
    private String currentBadgeLevel;
    
    @Schema(description = "Points needed to reach the next badge level", example = "250", minimum = "0")
    private Integer pointsToNextLevel;
    
    @Schema(description = "Next badge level the user can achieve", example = "SILVER",
            allowableValues = {"BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND", "MASTER"})
    private String nextBadgeLevel;
    
    @Schema(description = "Current login streak in days", example = "7", minimum = "0")
    private Integer loginStreak;
    
    @Schema(description = "Number of courses completed by the user", example = "5", minimum = "0")
    private Integer completedCourses;
    
    @Schema(description = "Average score across all completed courses", example = "85.5", minimum = "0", maximum = "100")
    private Double averageScore;
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public String getCurrentBadgeLevel() {
        return currentBadgeLevel;
    }
    
    public void setCurrentBadgeLevel(String currentBadgeLevel) {
        this.currentBadgeLevel = currentBadgeLevel;
    }
    
    public Integer getPointsToNextLevel() {
        return pointsToNextLevel;
    }
    
    public void setPointsToNextLevel(Integer pointsToNextLevel) {
        this.pointsToNextLevel = pointsToNextLevel;
    }
    
    public String getNextBadgeLevel() {
        return nextBadgeLevel;
    }
    
    public void setNextBadgeLevel(String nextBadgeLevel) {
        this.nextBadgeLevel = nextBadgeLevel;
    }
    
    public Integer getLoginStreak() {
        return loginStreak;
    }
    
    public void setLoginStreak(Integer loginStreak) {
        this.loginStreak = loginStreak;
    }
    
    public Integer getCompletedCourses() {
        return completedCourses;
    }
    
    public void setCompletedCourses(Integer completedCourses) {
        this.completedCourses = completedCourses;
    }
    
    public Double getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}