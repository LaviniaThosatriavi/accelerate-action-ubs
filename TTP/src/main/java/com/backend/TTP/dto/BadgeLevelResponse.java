package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing all available badge levels and their requirements")
public class BadgeLevelResponse {
    
    @Schema(description = "List of all badge levels with their requirements", 
            required = true)
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
    @Schema(description = "Badge level information with requirements and rewards")
    public static class BadgeLevel {
        
        @Schema(description = "Badge level name", 
                example = "BRONZE",
                allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"})
        private String level;
        
        @Schema(description = "Minimum points required to achieve this badge level", 
                example = "100", 
                minimum = "0")
        private Integer minPoints;
        
        @Schema(description = "Maximum points for this badge level (null for highest level)", 
                example = "499", 
                minimum = "0")
        private Integer maxPoints;
        
        @Schema(description = "Description of the badge level and its benefits", 
                example = "Awarded for completing your first few courses and earning 100+ points")
        private String description;
        
        @Schema(description = "URL to the badge icon image", 
                example = "https://api.ttp.com/badges/bronze.png",
                format = "uri")
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