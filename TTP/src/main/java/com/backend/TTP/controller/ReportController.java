package com.backend.TTP.controller;

import com.backend.TTP.dto.report.*;
import com.backend.TTP.model.User;
import com.backend.TTP.service.report.ReportGeneratorService;
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
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ReportGeneratorService reportGeneratorService;
    
    /**
     * Get comprehensive learning report with all analysis
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<ComprehensiveReport> getComprehensiveReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<LearningOverviewReport> getOverviewReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<SkillAnalysisReport> getSkillAnalysisReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<ConsistencyReport> getConsistencyReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<TimeManagementReport> getTimeManagementReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<CompetitiveReport> getCompetitiveReport(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<QuickInsightsResponse> getQuickInsights(@AuthenticationPrincipal User user) {
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
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}