package com.backend.TTP.controller;

import com.backend.TTP.dto.report.*;
import com.backend.TTP.model.User;
import com.backend.TTP.service.report.ReportGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "Learning Reports & Analytics", description = "Comprehensive learning analytics and reporting system for tracking progress, performance, and insights")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportGeneratorService reportGeneratorService;
    
    /**
     * Get comprehensive learning report with all analysis
     */
    @GetMapping("/comprehensive")
    @Operation(summary = "Get comprehensive learning report", 
               description = "Generate a complete learning analytics report including performance metrics, skill analysis, time management, and competitive insights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comprehensive report generated successfully",
                    content = @Content(schema = @Schema(implementation = ComprehensiveReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate comprehensive report")
    })
    public ResponseEntity<ComprehensiveReport> getComprehensiveReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating comprehensive report for user: {}", user.getUsername());
            ComprehensiveReport report = reportGeneratorService.generateComprehensiveReport(user);
            logger.info("Comprehensive report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating comprehensive report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating comprehensive report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get learning overview report
     */
    @GetMapping("/overview")
    @Operation(summary = "Get learning overview report", 
               description = "Generate a high-level overview of learning progress, achievements, and performance metrics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overview report generated successfully",
                    content = @Content(schema = @Schema(implementation = LearningOverviewReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate overview report")
    })
    public ResponseEntity<LearningOverviewReport> getOverviewReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating overview report for user: {}", user.getUsername());
            LearningOverviewReport report = reportGeneratorService.generateOverviewReport(user);
            logger.info("Overview report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating overview report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating overview report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get skill analysis report
     */
    @GetMapping("/skills")
    @Operation(summary = "Get skill analysis report", 
               description = "Generate detailed analysis of skill development, strengths, weaknesses, and improvement recommendations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skill analysis report generated successfully",
                    content = @Content(schema = @Schema(implementation = SkillAnalysisReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate skill analysis report")
    })
    public ResponseEntity<SkillAnalysisReport> getSkillAnalysisReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating skill analysis report for user: {}", user.getUsername());
            SkillAnalysisReport report = reportGeneratorService.generateSkillAnalysisReport(user);
            logger.info("Skill analysis report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating skill analysis report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating skill analysis report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get consistency analysis report
     */
    @GetMapping("/consistency")
    @Operation(summary = "Get learning consistency report", 
               description = "Analyze learning patterns, consistency metrics, streak tracking, and habit formation insights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consistency report generated successfully",
                    content = @Content(schema = @Schema(implementation = ConsistencyReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate consistency report")
    })
    public ResponseEntity<ConsistencyReport> getConsistencyReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating consistency report for user: {}", user.getUsername());
            ConsistencyReport report = reportGeneratorService.generateConsistencyReport(user);
            logger.info("Consistency report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating consistency report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating consistency report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get time management report
     */
    @GetMapping("/time-management")
    @Operation(summary = "Get time management report", 
               description = "Analyze time allocation, productivity patterns, and efficiency metrics for learning activities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time management report generated successfully",
                    content = @Content(schema = @Schema(implementation = TimeManagementReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate time management report")
    })
    public ResponseEntity<TimeManagementReport> getTimeManagementReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating time management report for user: {}", user.getUsername());
            TimeManagementReport report = reportGeneratorService.generateTimeManagementReport(user);
            logger.info("Time management report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating time management report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating time management report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get competitive analysis report
     */
    @GetMapping("/competitive")
    @Operation(summary = "Get competitive analysis report", 
               description = "Compare user performance against peers, leaderboard rankings, and competitive insights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitive report generated successfully",
                    content = @Content(schema = @Schema(implementation = CompetitiveReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate competitive report")
    })
    public ResponseEntity<CompetitiveReport> getCompetitiveReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating competitive report for user: {}", user.getUsername());
            CompetitiveReport report = reportGeneratorService.generateCompetitiveReport(user);
            logger.info("Competitive report generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(report);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating competitive report: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating competitive report: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Get quick insights for dashboard
     */
    @GetMapping("/quick-insights")
    @Operation(summary = "Get quick learning insights", 
               description = "Generate quick insights and key metrics for dashboard display, including completion rates, streaks, and top recommendations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quick insights generated successfully",
                    content = @Content(schema = @Schema(implementation = QuickInsightsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error - failed to generate quick insights")
    })
    public ResponseEntity<QuickInsightsResponse> getQuickInsights(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                logger.error("Authentication principal is null");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
            }
            
            logger.info("Generating quick insights for user: {}", user.getUsername());
            
            // Generate a simplified overview for quick dashboard display
            LearningOverviewReport overview = reportGeneratorService.generateOverviewReport(user);
            
            QuickInsightsResponse insights = new QuickInsightsResponse();
            insights.setUsername(user.getUsername());
            insights.setCompletionRate(overview.getPerformance().getCompletionRate());
            insights.setAverageScore(overview.getPerformance().getAverageScore());
            insights.setLoginStreak(overview.getPerformance().getLoginStreak());
            insights.setCurrentBadgeLevel(overview.getPerformance().getCurrentBadgeLevel());
            insights.setTopStrength(overview.getStrengths().isEmpty() ? "Active learner" : overview.getStrengths().get(0));
            insights.setTopRecommendation(overview.getRecommendations().isEmpty() ? "Keep learning!" : overview.getRecommendations().get(0));
            
            logger.info("Quick insights generated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(insights);
            
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating quick insights: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error generating quick insights: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Handle OPTIONS preflight requests
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(hidden = true)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}