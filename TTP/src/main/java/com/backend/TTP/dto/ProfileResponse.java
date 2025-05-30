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
    
    // Add explicit getters/setters if Lombok isn't working
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
}