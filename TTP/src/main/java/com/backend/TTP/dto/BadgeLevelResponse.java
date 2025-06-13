package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeLevelResponse {
    private List<BadgeLevel> badgeLevels;
    
    public List<BadgeLevel> getBadgeLevels() {
        return badgeLevels;
    }
    
    public void setBadgeLevels(List<BadgeLevel> badgeLevels) {
        this.badgeLevels = badgeLevels;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BadgeLevel {
        private String level;
        private Integer minPoints;
        private Integer maxPoints;
        private String description;
        private String iconUrl;
        
        public String getLevel() {
            return level;
        }
        
        public void setLevel(String level) {
            this.level = level;
        }
        
        public Integer getMinPoints() {
            return minPoints;
        }
        
        public void setMinPoints(Integer minPoints) {
            this.minPoints = minPoints;
        }
        
        public Integer getMaxPoints() {
            return maxPoints;
        }
        
        public void setMaxPoints(Integer maxPoints) {
            this.maxPoints = maxPoints;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getIconUrl() {
            return iconUrl;
        }
        
        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }
    }
}
