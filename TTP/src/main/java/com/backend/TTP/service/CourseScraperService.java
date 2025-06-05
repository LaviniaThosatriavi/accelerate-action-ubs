package com.backend.TTP.service;

import com.backend.TTP.model.Course;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CourseScraperService {
    private static final Logger logger = LoggerFactory.getLogger(CourseScraperService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Cache for validated URLs to avoid repeated checks
    private final Map<String, Boolean> urlValidationCache = new ConcurrentHashMap<>();
    
    // ExecutorService for parallel URL validation
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    /**
     * Search for courses across multiple platforms
     */
    public List<Course> searchCoursesOnline(Set<String> keywords) {
        List<Course> results = new ArrayList<>();
        List<Future<List<Course>>> futures = new ArrayList<>();
        
        for (String keyword : keywords) {
            // Use CompletableFuture to search platforms in parallel
            futures.add(executorService.submit(() -> searchYouTubeCourses(keyword)));
            futures.add(executorService.submit(() -> searchUdemyCourses(keyword)));
            futures.add(executorService.submit(() -> searchCourseraCourses(keyword)));
            futures.add(executorService.submit(() -> searchLinkedInLearningCourses(keyword)));
            futures.add(executorService.submit(() -> searchPluralSightCourses(keyword)));
        }
        
        // Collect results from all futures
        for (Future<List<Course>> future : futures) {
            try {
                List<Course> courses = future.get(10, TimeUnit.SECONDS);
                results.addAll(courses);
            } catch (Exception e) {
                logger.error("Error retrieving course search results", e);
            }
        }
        
        // Validate all URLs in parallel
        validateUrlsInParallel(results);
        
        return results;
    }
    
    /**
     * Validate URLs in parallel for all courses
     */
    private void validateUrlsInParallel(List<Course> courses) {
        List<Future<Boolean>> validationFutures = new ArrayList<>();
        Map<Course, Future<Boolean>> courseValidationMap = new HashMap<>();
        
        // Submit validation tasks
        for (Course course : courses) {
            final String url = course.getUrl();
            
            // Skip if URL already in cache
            if (urlValidationCache.containsKey(url)) {
                continue;
            }
            
            Future<Boolean> future = executorService.submit(() -> isValidUrl(url));
            validationFutures.add(future);
            courseValidationMap.put(course, future);
        }
        
        // Process validation results
        Iterator<Course> iterator = courses.iterator();
        while (iterator.hasNext()) {
            Course course = iterator.next();
            String url = course.getUrl();
            
            // If URL is in cache and invalid, remove the course
            if (urlValidationCache.containsKey(url) && !urlValidationCache.get(url)) {
                iterator.remove();
                continue;
            }
            
            // If we have a validation future for this course
            Future<Boolean> future = courseValidationMap.get(course);
            if (future != null) {
                try {
                    boolean isValid = future.get(5, TimeUnit.SECONDS);
                    urlValidationCache.put(url, isValid);
                    
                    if (!isValid) {
                        iterator.remove();
                    }
                } catch (Exception e) {
                    logger.warn("Error validating URL: {}", url, e);
                    // Keep the course if validation times out
                }
            }
        }
    }
    
    /**
     * Search YouTube for educational content
     */
    private List<Course> searchYouTubeCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        try {
            // For demonstration, we'll use public search page
            String searchUrl = "https://www.youtube.com/results?search_query=" + 
                              keyword.replace(" ", "+") + "+course+tutorial";
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            // Create course entries for specific videos (these are popular educational channels)
            String[] channelIds = {
                "UCWv7vMbMWH4-V0ZXdmDpPBA", // Programming with Mosh
                "UCCezIgC97PvUuR4_gbFUs5g", // Corey Schafer
                "UC8butISFwT-Wl7EV0hUK0BQ", // freeCodeCamp
                "UCvjgXvBlbQiydffZU7m1_aw", // The Coding Train
                "UCW5YeuERMmlnqo4oq8vwUpg"  // The Net Ninja
            };
            
            for (String channelId : channelIds) {
                String videoSearchUrl = "https://www.youtube.com/c/" + channelId + "/search?query=" + 
                                      keyword.replace(" ", "+");
                
                Course course = new Course();
                course.setTitle(keyword + " Tutorial Videos");
                course.setDescription("Curated video tutorials about " + keyword + " from top educational channels");
                course.setPlatform("YouTube");
                course.setUrl(searchUrl); // Main search URL
                course.setEstimatedHours(5); // Average for tutorial series
                course.setSkillLevel("ALL LEVELS");
                course.setCategory(keyword);
                
                Set<String> tags = new HashSet<>();
                tags.add(keyword.toLowerCase());
                tags.add("video");
                tags.add("tutorial");
                course.setTags(tags);
                
                courses.add(course);
                
                // Only add one YouTube entry per keyword to avoid duplication
                break;
            }
            
        } catch (Exception e) {
            logger.error("Error searching YouTube for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    /**
     * Search Udemy for courses
     */
    private List<Course> searchUdemyCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        try {
            String searchUrl = "https://www.udemy.com/courses/search/?q=" + 
                              keyword.replace(" ", "+") + "&src=ukw";
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            Course course = new Course();
            course.setTitle(keyword + " Courses on Udemy");
            course.setDescription("Comprehensive " + keyword + " courses on Udemy with instructor-led training");
            course.setPlatform("Udemy");
            course.setUrl(searchUrl);
            course.setEstimatedHours(12); // Average Udemy course length
            course.setSkillLevel("VARIES");
            course.setCategory(keyword);
            
            Set<String> tags = new HashSet<>();
            tags.add(keyword.toLowerCase());
            tags.add("course");
            tags.add("udemy");
            course.setTags(tags);
            
            courses.add(course);
            
        } catch (Exception e) {
            logger.error("Error searching Udemy for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    /**
     * Search Coursera for courses
     */
    private List<Course> searchCourseraCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        try {
            String searchUrl = "https://www.coursera.org/search?query=" + 
                              keyword.replace(" ", "+");
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            Course course = new Course();
            course.setTitle(keyword + " Courses on Coursera");
            course.setDescription("University-backed " + keyword + " courses and specializations on Coursera");
            course.setPlatform("Coursera");
            course.setUrl(searchUrl);
            course.setEstimatedHours(20); // Average for Coursera courses
            course.setSkillLevel("INTERMEDIATE");
            course.setCategory(keyword);
            
            Set<String> tags = new HashSet<>();
            tags.add(keyword.toLowerCase());
            tags.add("course");
            tags.add("coursera");
            tags.add("university");
            course.setTags(tags);
            
            courses.add(course);
            
        } catch (Exception e) {
            logger.error("Error searching Coursera for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    /**
     * Search LinkedIn Learning for courses
     */
    private List<Course> searchLinkedInLearningCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        try {
            String searchUrl = "https://www.linkedin.com/learning/search?keywords=" + 
                              keyword.replace(" ", "+");
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            Course course = new Course();
            course.setTitle(keyword + " Courses on LinkedIn Learning");
            course.setDescription("Professional " + keyword + " courses from industry experts on LinkedIn Learning");
            course.setPlatform("LinkedIn Learning");
            course.setUrl(searchUrl);
            course.setEstimatedHours(8); // Average for LinkedIn Learning
            course.setSkillLevel("PROFESSIONAL");
            course.setCategory(keyword);
            
            Set<String> tags = new HashSet<>();
            tags.add(keyword.toLowerCase());
            tags.add("course");
            tags.add("linkedin");
            tags.add("professional");
            course.setTags(tags);
            
            courses.add(course);
            
        } catch (Exception e) {
            logger.error("Error searching LinkedIn Learning for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    /**
     * Search Pluralsight for courses
     */
    private List<Course> searchPluralSightCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        try {
            String searchUrl = "https://www.pluralsight.com/search?q=" + 
                              keyword.replace(" ", "+") + "&categories=course";
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            Course course = new Course();
            course.setTitle(keyword + " Courses on Pluralsight");
            course.setDescription("In-depth technical " + keyword + " training on Pluralsight for IT professionals");
            course.setPlatform("Pluralsight");
            course.setUrl(searchUrl);
            course.setEstimatedHours(10); // Average for Pluralsight courses
            course.setSkillLevel("TECHNICAL");
            course.setCategory(keyword);
            
            Set<String> tags = new HashSet<>();
            tags.add(keyword.toLowerCase());
            tags.add("course");
            tags.add("pluralsight");
            tags.add("technical");
            course.setTags(tags);
            
            courses.add(course);
            
        } catch (Exception e) {
            logger.error("Error searching Pluralsight for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    /**
     * Check if a URL is valid and the page exists
     * Uses connection timeout to avoid hanging
     */
    private boolean isValidUrl(String urlString) {
        // Check cache first
        if (urlValidationCache.containsKey(urlString)) {
            return urlValidationCache.get(urlString);
        }
        
        try {
            // Create a URL object
            URL url = new URL(urlString);
            
            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set request method
            connection.setRequestMethod("HEAD");
            
            // Set timeouts to avoid hanging
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            
            // Set user agent to avoid being blocked
            connection.setRequestProperty("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            
            // Get response code
            int responseCode = connection.getResponseCode();
            
            // 2xx or 3xx status codes indicate the page exists
            boolean isValid = (responseCode >= 200 && responseCode < 400);
            
            // Cache the result
            urlValidationCache.put(urlString, isValid);
            
            return isValid;
        } catch (Exception e) {
            logger.warn("Error validating URL: {}", urlString, e);
            
            // Cache the failure
            urlValidationCache.put(urlString, false);
            
            return false;
        }
    }
    
    /**
     * Add course results from LeetCode (for programming-related keywords)
     */
    private List<Course> searchLeetCodeExercises(String keyword) {
        List<Course> courses = new ArrayList<>();
        
        // Only search LeetCode for programming-related keywords
        Set<String> programmingKeywords = new HashSet<>(Arrays.asList(
            "algorithm", "data structure", "programming", "coding", "java", 
            "python", "javascript", "leetcode", "interview", "coding challenge",
            "cpp", "c++", "arrays", "dynamic programming", "graph", "tree"
        ));
        
        if (!programmingKeywords.contains(keyword.toLowerCase())) {
            return courses;
        }
        
        try {
            String searchUrl = "https://leetcode.com/problemset/all/?search=" + 
                              keyword.replace(" ", "+");
            
            // Verify search page exists
            if (!isValidUrl(searchUrl)) {
                return courses;
            }
            
            Course course = new Course();
            course.setTitle(keyword + " Coding Exercises on LeetCode");
            course.setDescription("Practice " + keyword + " with coding exercises and challenges on LeetCode");
            course.setPlatform("LeetCode");
            course.setUrl(searchUrl);
            course.setEstimatedHours(15); // Average time to complete a set of exercises
            course.setSkillLevel("TECHNICAL");
            course.setCategory(keyword);
            
            Set<String> tags = new HashSet<>();
            tags.add(keyword.toLowerCase());
            tags.add("coding");
            tags.add("exercises");
            tags.add("practice");
            course.setTags(tags);
            
            courses.add(course);
            
        } catch (Exception e) {
            logger.error("Error searching LeetCode for keyword: {}", keyword, e);
        }
        
        return courses;
    }
    
    // Method to clean up resources when application shuts down
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}