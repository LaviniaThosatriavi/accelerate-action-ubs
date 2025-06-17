package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Quick insights response for dashboard display with key metrics and recommendations")
public class QuickInsightsResponse {
    
    @Schema(description = "Username of the user", 
            example = "john_doe",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    
    @Schema(description = "Course completion rate as a percentage", 
            example = "75.5", 
            minimum = "0", 
            maximum = "100",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Double completionRate;
    
    @Schema(description = "Average score across all completed courses", 
            example = "87.5", 
            minimum = "0", 
            maximum = "100",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Double averageScore;
    
    @Schema(description = "Current consecutive daily login streak", 
            example = "14", 
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer loginStreak;
    
    @Schema(description = "Current badge level achieved", 
            example = "SILVER",
            allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
            accessMode = Schema.AccessMode.READ_ONLY)
    private String currentBadgeLevel;
    
    @Schema(description = "Top identified strength of the user", 
            example = "Consistent daily learning habits",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String topStrength;
    
    @Schema(description = "Top recommendation for improvement", 
            example = "Focus on completing JavaScript fundamentals course",
            accessMode = Schema.AccessMode.READ_ONLY)
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