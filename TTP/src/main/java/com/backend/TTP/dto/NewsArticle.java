package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "News article information from external news sources")
public class NewsArticle {
    
    @Schema(description = "Title of the news article", 
            example = "AI Revolution in Software Development", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private String title;
    
    @Schema(description = "Direct link to the full article", 
            example = "https://techcrunch.com/2024/06/15/ai-revolution-software", 
            format = "uri",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String link;
    
    @Schema(description = "URL of the article's featured image", 
            example = "https://techcrunch.com/wp-content/uploads/2024/06/ai-coding.jpg", 
            format = "uri",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String image_url;
    
    @Schema(description = "Brief description or summary of the article", 
            example = "How artificial intelligence is transforming the way developers write and debug code",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String description;
    
    @Schema(description = "Full content or excerpt of the article", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private String content;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}