package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Course recommendations and references from external platforms")
public class CourseReferenceDTO {
    
    @Schema(description = "Course category or subject area", 
            example = "programming",
            allowableValues = {"programming", "design", "business", "data-science", "marketing", "photography"})
    private String category;
    
    @Schema(description = "URL to relevant Coursera courses", 
            example = "https://www.coursera.org/specializations/full-stack-react",
            format = "uri")
    private String courseraUrl;
    
    @Schema(description = "URL to FreeCodeCamp playlist or course", 
            example = "https://www.freecodecamp.org/learn/front-end-development-libraries/",
            format = "uri")
    private String freeCodeCampPlaylistUrl;
    
    @Schema(description = "List of suggested video URLs for supplementary learning", 
            example = "[\"https://youtube.com/watch?v=abc123\", \"https://youtube.com/watch?v=def456\"]")
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