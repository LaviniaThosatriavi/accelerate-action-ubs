package com.backend.TTP.controller;

import com.backend.TTP.dto.NewsDataResponse;
import com.backend.TTP.dto.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.HttpClientErrorException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.trending-url}")
    private String trendingUrl;
    
    @Value("${news.api.search-url}")
    private String searchUrl;

    private final RestTemplate restTemplate;

    public NewsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/trending")
    @Cacheable("trendingNews")
    public ResponseEntity<?> getTrendingTechNews() {
        try {
            String query = URLEncoder.encode("technology", StandardCharsets.UTF_8);
            String url = String.format("%s?apikey=%s&q=%s&language=en&size=10", 
                trendingUrl, apiKey, query);
            return fetchNewsData(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error constructing request: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Cacheable(value = "newsSearch", key = "#q")
    public ResponseEntity<?> searchNews(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Search query cannot be empty");
        }
        
        try {
            String encodedQuery = URLEncoder.encode(q.trim(), StandardCharsets.UTF_8);
            String url = String.format("%s?apikey=%s&q=%s&language=en&size=10", 
                searchUrl, apiKey, encodedQuery);
            return fetchNewsData(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing search: " + e.getMessage());
        }
    }

    private ResponseEntity<?> fetchNewsData(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<NewsDataResponse> response = 
                restTemplate.exchange(url, HttpMethod.GET, entity, NewsDataResponse.class);
            
            if (response.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Empty response from News API");
            }
            
            List<NewsArticle> results = response.getBody().getResults();
            if (results == null || results.isEmpty()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Collections.emptyList());
            }

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(results);

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body("API Error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Server Error: " + e.getMessage());
        }
    }
}