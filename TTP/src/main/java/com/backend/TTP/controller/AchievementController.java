package com.backend.TTP.controller;

import com.backend.TTP.dto.*;
import com.backend.TTP.model.User;
import com.backend.TTP.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = {"http://localhost:5173"}, 
            allowCredentials = "true",
            allowedHeaders = {"Authorization", "Content-Type", "Accept"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "Achievement Management", description = "Operations related to user achievements, badges, points, and leaderboards")
@SecurityRequirement(name = "bearerAuth")
public class AchievementController {
    
    @Autowired
    private AchievementService achievementService;
    
    /**
     * Get user's achievement profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user's achievement profile", 
               description = "Retrieve the authenticated user's complete achievement profile including total points, current badge level, and progress")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Achievement profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AchievementProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch achievement profile"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<AchievementProfileResponse> getAchievementProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            AchievementProfileResponse response = achievementService.getAchievementProfile(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all badge levels with requirements
     */
    @GetMapping("/badges")
    @Operation(summary = "Get all badge levels", 
               description = "Retrieve all available badge levels with their requirements and point thresholds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Badge levels retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BadgeLevelResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch badge levels")
    })
    public ResponseEntity<BadgeLevelResponse> getBadgeLevels() {
        try {
            BadgeLevelResponse response = achievementService.getBadgeLevels();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user's progress toward next badge level
     */
    @GetMapping("/progress")
    @Operation(summary = "Get user's badge progress", 
               description = "Retrieve the authenticated user's progress toward the next badge level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AchievementProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch progress"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<AchievementProfileResponse> getProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            AchievementProfileResponse response = achievementService.getAchievementProfile(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Trigger point calculation (called by other services)
     */
    @PostMapping("/calculate-points")
    @Operation(summary = "Calculate and record user points", 
               description = "Trigger point calculation and record daily login for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Points calculated and daily login recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to calculate points"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<Void> calculatePoints(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            // Record daily login when user activity is detected
            achievementService.recordDailyLogin(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get point earning history
     */
    @GetMapping("/points/history")
    @Operation(summary = "Get point earning history", 
               description = "Retrieve the authenticated user's point earning history within the specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PointHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch point history"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<PointHistoryResponse> getPointHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Start date for history filter (optional)", 
                      example = "2024-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date for history filter (optional)", 
                      example = "2024-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            PointHistoryResponse response = achievementService.getPointHistory(user, startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get leaderboard 
     */
    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", 
               description = "Retrieve the leaderboard for the specified period with ranking of users by points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LeaderboardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch leaderboard"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<LeaderboardResponse> getLeaderboard(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Leaderboard period", 
                      example = "daily", 
                      schema = @Schema(allowableValues = {"daily", "weekly", "monthly", "all-time"}))
            @RequestParam(defaultValue = "daily") String period,
            @Parameter(description = "Maximum number of users to return", 
                      example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            LeaderboardResponse response = achievementService.getLeaderboard(user, period, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current user's rank
     */
    @GetMapping("/leaderboard/rank")
    @Operation(summary = "Get current user's leaderboard rank", 
               description = "Retrieve the authenticated user's current rank and position on the leaderboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User rank retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LeaderboardUserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to fetch user rank"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<LeaderboardUserDTO> getCurrentUserRank(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Leaderboard period for rank calculation", 
                      example = "daily",
                      schema = @Schema(allowableValues = {"daily", "weekly", "monthly", "all-time"}))
            @RequestParam(defaultValue = "daily") String period) {
        try {
            LeaderboardResponse leaderboard = achievementService.getLeaderboard(user, period, 1);
            return ResponseEntity.ok(leaderboard.getCurrentUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Manual trigger for daily leaderboard update (for admin/testing)
     */
    @PostMapping("/admin/update-leaderboard")
    @Operation(summary = "Update leaderboard (Admin)", 
               description = "Manually trigger the daily leaderboard update process. This endpoint is intended for administrative use and testing purposes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to update leaderboard"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<Void> updateLeaderboard() {
        try {
            achievementService.updateDailyLeaderboard();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * FIXED: Simple test endpoint to award test points
     */
    @PostMapping("/admin/test-points")
    @Operation(summary = "Award test points (Admin)", 
               description = "Award test points to the authenticated user for development and testing purposes. This endpoint shows before/after point totals.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test points awarded successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - unable to award test points"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required")
    })
    public ResponseEntity<String> awardTestPoints(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            // Get current total points before
            AchievementProfileResponse profileBefore = achievementService.getAchievementProfile(user);
            Integer beforePoints = profileBefore.getTotalPoints();
            
            // Award test points
            achievementService.awardPoints(user, "TEST", 50, "Test points for development", null);
            achievementService.recordDailyLogin(user); // Also record login
            
            // Get points after
            AchievementProfileResponse profileAfter = achievementService.getAchievementProfile(user);
            Integer afterPoints = profileAfter.getTotalPoints();
            
            return ResponseEntity.ok(String.format(
                "Test completed! User: %s (ID: %d)\n" +
                "Total Points: %d → %d\n" +
                "Leaderboard updated automatically\n" +
                "Frontend should show: %d total points",
                user.getUsername(), user.getId(),
                beforePoints, afterPoints,
                afterPoints
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
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