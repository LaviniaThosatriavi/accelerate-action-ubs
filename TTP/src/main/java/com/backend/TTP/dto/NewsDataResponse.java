package com.backend.TTP.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response wrapper for news API containing articles and pagination")
public class NewsDataResponse {
    
    @Schema(description = "List of news articles returned by the API", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<NewsArticle> results;
    
    @Schema(description = "Token or URL for fetching the next page of results", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String nextPage;

    // Getters and setters
    public List<NewsArticle> getResults() { return results; }
    public void setResults(List<NewsArticle> results) { this.results = results; }
    public String getNextPage() { return nextPage; }
    public void setNextPage(String nextPage) { this.nextPage = nextPage; }
}