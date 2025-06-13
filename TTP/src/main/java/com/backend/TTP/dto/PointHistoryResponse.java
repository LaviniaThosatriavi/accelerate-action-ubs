package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private List<PointHistoryItem> pointHistory;
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
    public static class PointHistoryItem {
        private String activityType;
        private Integer pointsEarned;
        private LocalDateTime earnedAt;
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