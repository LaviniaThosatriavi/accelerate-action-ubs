package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information for leaderboard display with ranking and achievements")
public class LeaderboardUserDTO {
    
    @Schema(description = "Unique user identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    
    @Schema(description = "Username of the user", example = "john_doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    
    @Schema(description = "Total points earned by the user in the specified period", 
            example = "1250", 
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer points;
    
    @Schema(description = "Current rank/position on the leaderboard", 
            example = "3", 
            minimum = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer rank;
    
    @Schema(description = "Current badge level of the user", 
            example = "SILVER",
            allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
            accessMode = Schema.AccessMode.READ_ONLY)
    private String badgeLevel;
    
    @Schema(description = "Flag indicating if this is the current authenticated user", 
            example = "false",
            defaultValue = "false",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isCurrentUser = false;
    
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
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Integer getRank() {
        return rank;
    }
    
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    
    public String getBadgeLevel() {
        return badgeLevel;
    }
    
    public void setBadgeLevel(String badgeLevel) {
        this.badgeLevel = badgeLevel;
    }
    
    public Boolean getIsCurrentUser() {
        return isCurrentUser;
    }
    
    public void setIsCurrentUser(Boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }
}