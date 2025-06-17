package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "Request to create or update user profile with learning preferences")
public class ProfileRequest {
    
    @Schema(description = "User's current career stage or experience level", 
            example = "intermediate",
            allowableValues = {"beginner", "intermediate", "advanced", "expert"},
            defaultValue = "beginner")
    private String careerStage;
    
    @Schema(description = "List of user's current skills and technologies", 
            example = "[\"JavaScript\", \"React\", \"Node.js\", \"Python\"]")
    @Size(max = 20, message = "Maximum 20 skills allowed")
    private List<String> skills;
    
    @Schema(description = "User's learning goals and objectives", 
            example = "Learn full-stack development to become a software engineer",
            maxLength = 500,
            defaultValue = "improve skills")
    @Size(max = 500, message = "Goals description cannot exceed 500 characters")
    private String goals;
    
    @Schema(description = "Number of hours per week the user can dedicate to learning", 
            example = "10",
            minimum = "1",
            maximum = "168",
            defaultValue = "5")
    @Min(value = 1, message = "Hours per week must be at least 1")
    @Max(value = 168, message = "Hours per week cannot exceed 168 (total hours in a week)")
    private Integer hoursPerWeek;
    
    // Add explicit getters/setters if Lombok isn't working
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
}