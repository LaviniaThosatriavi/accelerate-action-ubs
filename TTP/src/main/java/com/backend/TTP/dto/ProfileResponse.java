package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response with learning preferences and achievement data")
public class ProfileResponse {
    
    @Schema(description = "Unique profile identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "User's current career stage or experience level", 
            example = "intermediate",
            allowableValues = {"beginner", "intermediate", "advanced", "expert"})
    private String careerStage;
    
    @Schema(description = "List of user's current skills and technologies", 
            example = "[\"JavaScript\", \"React\", \"Node.js\", \"Python\"]")
    private List<String> skills;
    
    @Schema(description = "User's learning goals and objectives", 
            example = "Learn full-stack development to become a software engineer")
    private String goals;
    
    @Schema(description = "Number of hours per week the user can dedicate to learning", 
            example = "10", 
            minimum = "1", 
            maximum = "168")
    private Integer hoursPerWeek;
    
    @Schema(description = "Personalized learning path based on user's profile", 
            example = "Full-Stack JavaScript Developer",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String learningPath;
    
    // Achievement fields with safe defaults
    @Schema(description = "Total points earned by the user", 
            example = "1250", 
            minimum = "0",
            defaultValue = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer totalPoints = 0;
    
    @Schema(description = "Current badge level achieved by the user", 
            example = "SILVER",
            allowableValues = {"NOVICE", "BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
            defaultValue = "NOVICE",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String currentBadgeLevel = "NOVICE";
    
    @Schema(description = "Current daily login streak", 
            example = "7", 
            minimum = "0",
            defaultValue = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
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