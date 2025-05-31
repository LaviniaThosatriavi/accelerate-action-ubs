package com.backend.TTP.controller;

import com.backend.TTP.dto.NewsDataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.trending-url}")
    private String trendingUrl;

    @Value("${news.api.search-url}")
    private String searchUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/trending")
    @Cacheable("trendingNews")
    public ResponseEntity<?> getTrendingTechNews() {
        String url = trendingUrl + "?apikey=" + apiKey + 
            "&category=technology&language=en&size=10";
        return fetchNewsData(url);
    }

    @GetMapping("/search")
    @Cacheable("newsSearch")
    public ResponseEntity<?> searchNews(@RequestParam String query) {
        String url = searchUrl + "?apikey=" + apiKey + 
            "&q=" + query + "&language=en&size=10";
        return fetchNewsData(url);
    }

    private ResponseEntity<?> fetchNewsData(String url) {
        try {
            ResponseEntity<NewsDataResponse> response = restTemplate.getForEntity(url, NewsDataResponse.class);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody().getResults());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching news: " + e.getMessage());
        }
    }
}