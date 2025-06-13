package com.backend.TTP.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String careerStage;
    private List<String> skills;
    private String goals;
    private Integer hoursPerWeek;
    private String learningPath;
    
    // Achievement fields with safe defaults
    private Integer totalPoints = 0;
    private String currentBadgeLevel = "NOVICE";
    private Integer loginStreak = 0;
    
    // Explicit getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCareerStage() {
        return careerStage;
    }
    
    public void setCareerStage(String careerStage) {
        this.careerStage = careerStage;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public String getGoals() {
        return goals;
    }
    
    public void setGoals(String goals) {
        this.goals = goals;
    }
    
    public Integer getHoursPerWeek() {
        return hoursPerWeek;
    }
    
    public void setHoursPerWeek(Integer hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }
    
    public String getLearningPath() {
        return learningPath;
    }
    
    public void setLearningPath(String learningPath) {
        this.learningPath = learningPath;
    }
    
    public Integer getTotalPoints() {
        return totalPoints != null ? totalPoints : 0;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints != null ? totalPoints : 0;
    }
    
    public String getCurrentBadgeLevel() {
        return currentBadgeLevel != null ? currentBadgeLevel : "NOVICE";
    }
    
    public void setCurrentBadgeLevel(String currentBadgeLevel) {
        this.currentBadgeLevel = currentBadgeLevel != null ? currentBadgeLevel : "NOVICE";
    }
    
    public Integer getLoginStreak() {
        return loginStreak != null ? loginStreak : 0;
    }
    
    public void setLoginStreak(Integer loginStreak) {
        this.loginStreak = loginStreak != null ? loginStreak : 0;
    }
}