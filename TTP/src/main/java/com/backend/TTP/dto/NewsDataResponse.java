package com.backend.TTP.dto;

import java.util.List;

public class NewsDataResponse {
    private List<NewsArticle> results;
    private String nextPage;

    // Getters and setters
    public List<NewsArticle> getResults() { return results; }
    public void setResults(List<NewsArticle> results) { this.results = results; }
    public String getNextPage() { return nextPage; }
    public void setNextPage(String nextPage) { this.nextPage = nextPage; }
}
