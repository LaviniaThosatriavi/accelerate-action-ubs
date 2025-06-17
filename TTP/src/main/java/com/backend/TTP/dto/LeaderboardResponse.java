package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Leaderboard response containing top users and current user ranking")
public class LeaderboardResponse {
    
    @Schema(description = "List of top-ranking users on the leaderboard", 
            required = true,
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<LeaderboardUserDTO> topUsers;
    
    @Schema(description = "Current user's ranking and position on the leaderboard", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private LeaderboardUserDTO currentUser;
    
    @Schema(description = "Time period for the leaderboard ranking", 
            example = "daily",
            allowableValues = {"daily", "weekly", "monthly", "all-time"},
            accessMode = Schema.AccessMode.READ_ONLY)
    private String period; // daily, weekly, monthly
    
    public List<LeaderboardUserDTO> getTopUsers() {
        return topUsers;
    }
    
    public void setTopUsers(List<LeaderboardUserDTO> topUsers) {
        this.topUsers = topUsers;
    }
    
    public LeaderboardUserDTO getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(LeaderboardUserDTO currentUser) {
        this.currentUser = currentUser;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
}