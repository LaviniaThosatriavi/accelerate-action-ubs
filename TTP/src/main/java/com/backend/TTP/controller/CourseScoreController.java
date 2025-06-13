package com.backend.TTP.controller;

import com.backend.TTP.dto.CourseScoreRequest;
import com.backend.TTP.model.CourseScore;
import com.backend.TTP.model.User;
import com.backend.TTP.service.AchievementService;
import com.backend.TTP.service.CourseScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-scores")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseScoreController {
    
    @Autowired
    private CourseScoreService courseScoreService;
    
    @Autowired
    private AchievementService achievementService;
    
    /**
     * Record final score for completed course
     */
    @PostMapping
    public ResponseEntity<CourseScore> recordCourseScore(
            @AuthenticationPrincipal User user,
            @RequestBody CourseScoreRequest request) {
        try {
            CourseScore courseScore = achievementService.recordCourseScore(user, request);
            return ResponseEntity.ok(courseScore);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
            CourseScore courseScore = courseScoreService.getCourseScore(user, courseId);
            return ResponseEntity.ok(courseScore);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all course scores for authenticated user
     */
    @GetMapping("/user-scores")
    public ResponseEntity<List<CourseScore>> getUserScores(@AuthenticationPrincipal User user) {
        try {
            List<CourseScore> scores = courseScoreService.getUserScores(user);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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