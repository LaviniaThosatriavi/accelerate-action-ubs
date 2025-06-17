package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickInsightsResponse {
    private String username;
    private Double completionRate;
    private Double averageScore;
    private Integer loginStreak;
    private String currentBadgeLevel;
    private String topStrength;
    private String topRecommendation;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }
    
    public Double getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
    
    public Integer getLoginStreak() {
        return loginStreak;
    }
    
    public void setLoginStreak(Integer loginStreak) {
        this.loginStreak = loginStreak;
    }
    
    public String getCurrentBadgeLevel() {
        return currentBadgeLevel;
    }
    
    public void setCurrentBadgeLevel(String currentBadgeLevel) {
        this.currentBadgeLevel = currentBadgeLevel;
    }
    
    public String getTopStrength() {
        return topStrength;
    }
    
    public void setTopStrength(String topStrength) {
        this.topStrength = topStrength;
    }
    
    public String getTopRecommendation() {
        return topRecommendation;
    }
    
    public void setTopRecommendation(String topRecommendation) {
        this.topRecommendation = topRecommendation;
    }
}