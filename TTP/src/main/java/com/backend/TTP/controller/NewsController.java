package com.backend.TTP.controller;

import com.backend.TTP.dto.NewsDataResponse;
import com.backend.TTP.dto.NewsArticle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "News & Articles", description = "Operations for fetching trending technology news and searching for specific topics")
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
    @Operation(summary = "Get trending technology news", 
               description = "Retrieve the latest trending technology news articles from external news sources. Results are cached for improved performance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trending tech news retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NewsArticle.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid API key"),
            @ApiResponse(responseCode = "429", description = "Too many requests - rate limit exceeded"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to fetch news",
                    content = @Content(schema = @Schema(type = "string", example = "Error constructing request: Connection timeout"))),
            @ApiResponse(responseCode = "502", description = "Bad gateway - external news API unavailable",
                    content = @Content(schema = @Schema(type = "string", example = "Empty response from News API")))
    })
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
    @Operation(summary = "Search news articles", 
               description = "Search for news articles based on a specific query. Results are cached by search term for improved performance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "News search completed successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NewsArticle.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - search query cannot be empty",
                    content = @Content(schema = @Schema(type = "string", example = "Search query cannot be empty"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid API key"),
            @ApiResponse(responseCode = "429", description = "Too many requests - rate limit exceeded"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to process search",
                    content = @Content(schema = @Schema(type = "string", example = "Error processing search: Connection timeout"))),
            @ApiResponse(responseCode = "502", description = "Bad gateway - external news API error",
                    content = @Content(schema = @Schema(type = "string", example = "API Error: Invalid request format")))
    })
    public ResponseEntity<?> searchNews(
            @Parameter(description = "Search query for news articles", 
                      required = true, 
                      example = "artificial intelligence")
            @RequestParam String q) {
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

    @Operation(hidden = true)
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