package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardUserDTO {
    private Long userId;
    private String username;
    private Integer points;
    private Integer rank;
    private String badgeLevel;
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
