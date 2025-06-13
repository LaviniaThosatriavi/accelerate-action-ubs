package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementProfileResponse {
    private Long userId;
    private String username;
    private Integer totalPoints;
    private String currentBadgeLevel;
    private Integer pointsToNextLevel;
    private String nextBadgeLevel;
    private Integer loginStreak;
    private Integer completedCourses;
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