package com.backend.TTP.controller;

import com.backend.TTP.dto.EnrolledCourseDTO;
import com.backend.TTP.dto.EnrollmentRequest;
import com.backend.TTP.dto.ExternalCourseEnrollmentDTO;
import com.backend.TTP.dto.ProgressUpdateRequest;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.EnrolledCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrolled-courses")
public class EnrolledCourseController {
    
    @Autowired
    private EnrolledCourseService enrolledCourseService;

    @Autowired
    private UserRepository userRepository;

    
    /**
     * Get all enrolled courses for current user
     */
    @GetMapping
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrolledCourses() {
        User user = getCurrentUser();
        List<EnrolledCourseDTO> enrolledCourses = enrolledCourseService.getEnrolledCourses(user);
        return ResponseEntity.ok(enrolledCourses);
    }
    
    /**
     * Enroll in a course
     */
    @PostMapping("/enroll")
    public ResponseEntity<EnrolledCourseDTO> enrollCourse(@RequestBody EnrollmentRequest request) {
        User user = getCurrentUser();
        EnrolledCourseDTO enrolledCourse = enrolledCourseService.enrollCourse(user, request);
        return ResponseEntity.ok(enrolledCourse);
    }
    
    /**
     * Enroll in an external course (Coursera, YouTube, etc.)
     */
    @PostMapping("/enroll-external")
    public ResponseEntity<EnrolledCourseDTO> enrollExternalCourse(
            @RequestBody ExternalCourseEnrollmentDTO request) {
        User user = getCurrentUser();
        EnrolledCourseDTO enrolledCourse = enrolledCourseService.enrollExternalCourse(user, request);
        return ResponseEntity.ok(enrolledCourse);
    }
    
    /**
     * Update progress for an enrolled course
     */
    @PutMapping("/progress")
    public ResponseEntity<EnrolledCourseDTO> updateProgress(@RequestBody ProgressUpdateRequest request) {
        User user = getCurrentUser();
        EnrolledCourseDTO updatedCourse = enrolledCourseService.updateProgress(user, request);
        return ResponseEntity.ok(updatedCourse);
    }
    
    /**
     * Get enrolled courses by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrolledCoursesByStatus(@PathVariable String status) {
        User user = getCurrentUser();
        List<EnrolledCourseDTO> enrolledCourses = enrolledCourseService.getEnrolledCoursesByStatus(user, status);
        return ResponseEntity.ok(enrolledCourses);
    }
    
    /**
     * Get statistics about enrolled courses
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getEnrollmentStatistics() {
        User user = getCurrentUser();
        Map<String, Object> stats = enrolledCourseService.getEnrollmentStatistics(user);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Check if user has free time this week
     */
    @GetMapping("/has-available-time")
    public ResponseEntity<Boolean> hasAvailableTimeThisWeek() {
        User user = getCurrentUser();
        boolean hasTime = enrolledCourseService.hasAvailableTimeThisWeek(user);
        return ResponseEntity.ok(hasTime);
    }
    
    /**
     * Get supplementary videos based on user's learning path and available time
     */
    @GetMapping("/supplementary-videos/{category}")
    public ResponseEntity<List<String>> getSupplementaryVideos(@PathVariable String category) {
        User user = getCurrentUser();
        List<String> videos = enrolledCourseService.getSupplementaryVideos(user, category);
        return ResponseEntity.ok(videos);
    }
    
    /**
     * Helper method to get current authenticated user
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/total-hours-this-week")
    public ResponseEntity<Integer> getTotalHoursThisWeek(Principal principal) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(principal.getName());
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            User user = userOpt.get();
            int totalHours = enrolledCourseService.getTotalHoursThisWeek(user);
            return ResponseEntity.ok(totalHours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}