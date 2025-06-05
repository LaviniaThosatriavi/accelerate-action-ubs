package com.backend.TTP.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String platform;
    private String url;
    private Integer estimatedHours;
    private String skillLevel;
    private String category;
    private Boolean isExternal = true; // Indicates if this is an external course (Coursera, etc.)
    
    @ElementCollection
    private Set<String> tags = new HashSet<>();
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Integer getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public String getSkillLevel() {
        return skillLevel;
    }
    
    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
    
    public Boolean getIsExternal() {
        return isExternal;
    }
    
    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }
}