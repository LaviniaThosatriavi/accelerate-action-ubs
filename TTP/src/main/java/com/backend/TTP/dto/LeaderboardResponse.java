package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private List<LeaderboardUserDTO> topUsers;
    private LeaderboardUserDTO currentUser;
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
