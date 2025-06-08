package com.backend.TTP.dto;

import java.util.List;

public class CourseReferenceDTO {
    private String category;
    private String courseraUrl;
    private String freeCodeCampPlaylistUrl;
    private List<String> suggestedVideos;
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCourseraUrl() {
        return courseraUrl;
    }
    
    public void setCourseraUrl(String courseraUrl) {
        this.courseraUrl = courseraUrl;
    }
    
    public String getFreeCodeCampPlaylistUrl() {
        return freeCodeCampPlaylistUrl;
    }
    
    public void setFreeCodeCampPlaylistUrl(String freeCodeCampPlaylistUrl) {
        this.freeCodeCampPlaylistUrl = freeCodeCampPlaylistUrl;
    }
    
    public List<String> getSuggestedVideos() {
        return suggestedVideos;
    }
    
    public void setSuggestedVideos(List<String> suggestedVideos) {
        this.suggestedVideos = suggestedVideos;
    }
}