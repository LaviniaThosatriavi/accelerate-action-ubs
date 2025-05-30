package com.backend.TTP.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileRequest {
    private String careerStage;
    private List<String> skills;
    private String goals;
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