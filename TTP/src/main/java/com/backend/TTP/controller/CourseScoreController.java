package com.backend.TTP.controller;

import com.backend.TTP.dto.CourseScoreRequest;
import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.User;
import com.backend.TTP.service.AchievementService;
import com.backend.TTP.service.CourseScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/course-scores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class CourseScoreController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseScoreController.class);
    private final CourseScoreService courseScoreService;
    private final AchievementService achievementService;
    
    /**
     * Record final score for completed course
     */
    @PostMapping
    public ResponseEntity<CourseScore> recordCourseScore(
            @AuthenticationPrincipal User user,
            @RequestBody CourseScoreRequest request) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not authenticated"
                );
            }
            
            logger.info("Recording course score for user: {}", user.getUsername());
            logger.info("Request data: {}", request); // Add this log
            
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
    public ResponseEntity<CourseScore> getCourseScore(
            @AuthenticationPrincipal User user,
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
    public ResponseEntity<List<CourseScore>> getUserScores(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}