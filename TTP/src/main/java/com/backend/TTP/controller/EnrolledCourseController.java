package com.backend.TTP.controller;

import com.backend.TTP.dto.EnrolledCourseDTO;
import com.backend.TTP.dto.EnrollmentRequest;
import com.backend.TTP.dto.ExternalCourseEnrollmentDTO;
import com.backend.TTP.dto.ProgressUpdateRequest;
import com.backend.TTP.model.User;
import com.backend.TTP.repository.UserRepository;
import com.backend.TTP.service.EnrolledCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrolled-courses")
@Tag(name = "Enrolled Course Management", description = "Operations for managing course enrollments, progress tracking, and learning analytics")
@SecurityRequirement(name = "bearerAuth")
public class EnrolledCourseController {
    
    @Autowired
    private EnrolledCourseService enrolledCourseService;

    @Autowired
    private UserRepository userRepository;

    
    /**
     * Get all enrolled courses for current user
     */
    @GetMapping
    @Operation(summary = "Get all enrolled courses", 
               description = "Retrieve all courses that the authenticated user is currently enrolled in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrolled courses retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnrolledCourseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrolledCourses() {
        User user = getCurrentUser();
        List<EnrolledCourseDTO> enrolledCourses = enrolledCourseService.getEnrolledCourses(user);
        return ResponseEntity.ok(enrolledCourses);
    }
    
    /**
     * Enroll in a course
     */
    @PostMapping("/enroll")
    @Operation(summary = "Enroll in a course", 
               description = "Enroll the authenticated user in a course from the internal course catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course enrollment successful",
                    content = @Content(schema = @Schema(implementation = EnrolledCourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid enrollment data or already enrolled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User or course not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EnrolledCourseDTO> enrollCourse(
            @Parameter(description = "Course enrollment information", required = true)
            @Valid @RequestBody EnrollmentRequest request) {
        User user = getCurrentUser();
        EnrolledCourseDTO enrolledCourse = enrolledCourseService.enrollCourse(user, request);
        return ResponseEntity.ok(enrolledCourse);
    }
    
    /**
     * Enroll in an external course (Coursera, YouTube, etc.)
     */
    @PostMapping("/enroll-external")
    @Operation(summary = "Enroll in external course", 
               description = "Enroll the authenticated user in an external course (Coursera, YouTube, Udemy, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "External course enrollment successful",
                    content = @Content(schema = @Schema(implementation = EnrolledCourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid external course data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EnrolledCourseDTO> enrollExternalCourse(
            @Parameter(description = "External course enrollment information", required = true)
            @Valid @RequestBody ExternalCourseEnrollmentDTO request) {
        User user = getCurrentUser();
        EnrolledCourseDTO enrolledCourse = enrolledCourseService.enrollExternalCourse(user, request);
        return ResponseEntity.ok(enrolledCourse);
    }
    
    /**
     * Update progress for an enrolled course
     */
    @PutMapping("/progress")
    @Operation(summary = "Update course progress", 
               description = "Update the progress percentage for an enrolled course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course progress updated successfully",
                    content = @Content(schema = @Schema(implementation = EnrolledCourseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid progress data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User or enrolled course not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EnrolledCourseDTO> updateProgress(
            @Parameter(description = "Progress update information", required = true)
            @Valid @RequestBody ProgressUpdateRequest request) {
        User user = getCurrentUser();
        EnrolledCourseDTO updatedCourse = enrolledCourseService.updateProgress(user, request);
        return ResponseEntity.ok(updatedCourse);
    }
    
    /**
     * Get enrolled courses by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get courses by status", 
               description = "Retrieve enrolled courses filtered by their completion status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully by status",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnrolledCourseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid status parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EnrolledCourseDTO>> getEnrolledCoursesByStatus(
            @Parameter(description = "Course status filter", 
                      example = "IN_PROGRESS",
                      schema = @Schema(allowableValues = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED", "PAUSED"}))
            @PathVariable String status) {
        User user = getCurrentUser();
        List<EnrolledCourseDTO> enrolledCourses = enrolledCourseService.getEnrolledCoursesByStatus(user, status);
        return ResponseEntity.ok(enrolledCourses);
    }
    
    /**
     * Get statistics about enrolled courses
     */
    @GetMapping("/stats")
    @Operation(summary = "Get enrollment statistics", 
               description = "Retrieve comprehensive statistics about the user's course enrollments and progress")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment statistics retrieved successfully",
                    content = @Content(schema = @Schema(type = "object", 
                            example = "{\"totalEnrolled\": 5, \"completed\": 2, \"inProgress\": 3, \"averageProgress\": 65.4}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getEnrollmentStatistics() {
        User user = getCurrentUser();
        Map<String, Object> stats = enrolledCourseService.getEnrollmentStatistics(user);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Check if user has free time this week
     */
    @GetMapping("/has-available-time")
    @Operation(summary = "Check available time this week", 
               description = "Check if the user has available time slots this week for new learning activities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available time check completed",
                    content = @Content(schema = @Schema(implementation = Boolean.class, example = "true"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> hasAvailableTimeThisWeek() {
        User user = getCurrentUser();
        boolean hasTime = enrolledCourseService.hasAvailableTimeThisWeek(user);
        return ResponseEntity.ok(hasTime);
    }
    
    /**
     * Get supplementary videos based on user's learning path and available time
     */
    @GetMapping("/supplementary-videos/{category}")
    @Operation(summary = "Get supplementary videos by category", 
               description = "Retrieve recommended supplementary videos based on user's learning path and available time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplementary videos retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid category"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getSupplementaryVideos(
            @Parameter(description = "Video category for recommendations", 
                      example = "programming",
                      schema = @Schema(allowableValues = {"programming", "design", "business", "data-science", "general"}))
            @PathVariable String category) {
        User user = getCurrentUser();
        List<String> videos = enrolledCourseService.getSupplementaryVideos(user, category);
        return ResponseEntity.ok(videos);
    }
    
    @GetMapping("/total-hours-this-week")
    @Operation(summary = "Get total learning hours this week", 
               description = "Retrieve the total number of hours the user has spent learning this week")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total learning hours retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class, example = "15"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Integer> getTotalHoursThisWeek(
            @Parameter(hidden = true) Principal principal) {
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
    
    /**
     * Helper method to get current authenticated user
     */
    @Operation(hidden = true)
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}