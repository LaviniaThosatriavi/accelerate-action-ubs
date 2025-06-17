package com.backend.TTP.controller;

import com.backend.TTP.dto.CourseScoreRequest;
import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.User;
import com.backend.TTP.service.AchievementService;
import com.backend.TTP.service.CourseScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/course-scores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "Course Score Management", description = "Operations for recording and retrieving course completion scores")
@SecurityRequirement(name = "bearerAuth")
public class CourseScoreController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseScoreController.class);
    private final CourseScoreService courseScoreService;
    private final AchievementService achievementService;
    
    /**
     * Record final score for completed course
     */
    @PostMapping
    @Operation(summary = "Record course completion score", 
               description = "Record the final score for a completed course. This will trigger achievement calculations and point awards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course score recorded successfully",
                    content = @Content(schema = @Schema(implementation = CourseScore.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid course score data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to record course score")
    })
    public ResponseEntity<CourseScore> recordCourseScore(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Course score information", required = true)
            @Valid @RequestBody CourseScoreRequest request) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }
            
            logger.info("Recording course score for user: {}", user.getUsername());
            logger.info("Request data: {}", request);
            
            CourseScore courseScore = achievementService.recordCourseScore(user, request);
            logger.info("Course score recorded successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(courseScore);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error recording course score: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error recording course score: " + e.getMessage(),
                    e
            );
        }
    }
    
    /**
     * Get user's score for specific course
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get course score by course ID", 
               description = "Retrieve the authenticated user's score for a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course score retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CourseScore.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Course score not found for the specified course"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CourseScore> getCourseScore(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Course ID to retrieve score for", required = true, example = "123")
            @PathVariable Long courseId) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }
            
            logger.info("Getting course score for user: {} and course: {}", user.getUsername(), courseId);
            CourseScore courseScore = courseScoreService.getCourseScore(user, courseId);
            logger.info("Course score retrieved successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(courseScore);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error("Course score not found: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage(),
                    e
            );
        } catch (Exception e) {
            logger.error("Error getting course score: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error getting course score: " + e.getMessage(),
                    e
            );
        }
    }
    
    /**
     * Get all course scores for authenticated user
     */
    @GetMapping("/user-scores")
    @Operation(summary = "Get all user course scores", 
               description = "Retrieve all course scores for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User course scores retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CourseScore.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CourseScore>> getUserScores(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }
            
            logger.info("Getting user scores for user: {}", user.getUsername());
            List<CourseScore> scores = courseScoreService.getUserScores(user);
            logger.info("User scores retrieved successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(scores);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting user scores: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error getting user scores: " + e.getMessage(),
                    e
            );
        }
    }
    
    /**
     * Handle OPTIONS requests for CORS
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(hidden = true)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}