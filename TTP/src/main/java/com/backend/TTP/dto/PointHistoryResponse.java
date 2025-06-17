package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Point earning history response with detailed activity breakdown")
public class PointHistoryResponse {
    
    @Schema(description = "List of point earning activities in chronological order", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<PointHistoryItem> pointHistory;
    
    @Schema(description = "Total points accumulated by the user", 
            example = "1250", 
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalPoints;
    
    public List<PointHistoryItem> getPointHistory() {
        return pointHistory;
    }
    
    public void setPointHistory(List<PointHistoryItem> pointHistory) {
        this.pointHistory = pointHistory;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual point earning activity record")
    public static class PointHistoryItem {
        
        @Schema(description = "Type of activity that earned points", 
                example = "COURSE_COMPLETION",
                allowableValues = {"DAILY_LOGIN", "COURSE_COMPLETION", "GOAL_COMPLETION", "STREAK_BONUS", "ASSESSMENT_PASS", "BADGE_EARNED"})
        private String activityType;
        
        @Schema(description = "Number of points earned from this activity", 
                example = "50", 
                minimum = "1")
        private Integer pointsEarned;
        
        @Schema(description = "Timestamp when the points were earned", 
                example = "2024-06-15T14:30:00", 
                format = "date-time")
        private LocalDateTime earnedAt;
        
        @Schema(description = "Detailed description of the point-earning activity", 
                example = "Completed React Fundamentals course with 85% score")
        private String description;
        
        public String getActivityType() {
            return activityType;
        }
        
        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }
        
        public Integer getPointsEarned() {
            return pointsEarned;
        }
        
        public void setPointsEarned(Integer pointsEarned) {
            this.pointsEarned = pointsEarned;
        }
        
        public LocalDateTime getEarnedAt() {
            return earnedAt;
        }
        
        public void setEarnedAt(LocalDateTime earnedAt) {
            this.earnedAt = earnedAt;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}