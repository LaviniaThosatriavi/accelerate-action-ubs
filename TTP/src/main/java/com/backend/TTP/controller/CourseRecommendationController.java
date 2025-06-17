package com.backend.TTP.controller;

import com.backend.TTP.dto.CourseReferenceDTO;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.CourseRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-references")
@Tag(name = "Course Recommendation", description = "Operations for getting personalized course recommendations and platform references")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Get personalized course recommendations", 
               description = "Retrieve personalized course platform links and recommendations based on the user's profile, learning path, and preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course recommendations retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseReferenceDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to retrieve course references",
                    content = @Content(schema = @Schema(type = "object", 
                            example = "{\"error\": \"Failed to retrieve course references\", \"message\": \"Error details\", \"type\": \"RuntimeException\"}")))
    })
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
    @Operation(summary = "Test course recommendation service", 
               description = "Simple endpoint to verify that the course recommendation controller is functioning properly")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controller test successful",
                    content = @Content(schema = @Schema(type = "object", 
                            example = "{\"status\": \"Course references controller is working\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Collections.singletonMap("status", "Course references controller is working"));
    }
}