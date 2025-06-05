package com.backend.TTP.controller;

import com.backend.TTP.dto.CourseReferenceDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.CourseRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-references")
public class CourseRecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseRecommendationController.class);
    
    @Autowired
    private CourseRecommendationService recommendationService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get course platform links based on user profile and learning path
     */
    @GetMapping
    public ResponseEntity<?> getCourseReferences() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            logger.info("Fetching course references for user: {}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            List<CourseReferenceDTO> references = recommendationService.getCourseReferencesForUser(user);
            return ResponseEntity.ok(references);
        } catch (Exception e) {
            logger.error("Error retrieving course references", e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                        "error", "Failed to retrieve course references",
                        "message", e.getMessage(),
                        "type", e.getClass().getSimpleName()
                    ));
        }
    }
    
    
    /**
     * Simple test endpoint to verify controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Collections.singletonMap("status", "Course references controller is working"));
    }
}