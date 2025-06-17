package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Learning overview report with performance metrics and progress analysis")
public class LearningOverviewReport {
    
    @Schema(description = "Type of report generated", 
            example = "LEARNING_OVERVIEW",
            defaultValue = "LEARNING_OVERVIEW",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String reportType = "LEARNING_OVERVIEW";
    
    @Schema(description = "High-level summary of learning progress and achievements", 
            example = "Strong learner with 80% course completion rate and consistent daily engagement",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String summary;
    
    @Schema(description = "Detailed performance metrics and statistics", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private PerformanceMetrics performance;
    
    @Schema(description = "Identified learning strengths and areas of excellence", 
            example = "[\"Consistent daily learning habits\", \"High course completion rate\", \"Strong problem-solving skills\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> strengths;
    
    @Schema(description = "Areas needing improvement or development", 
            example = "[\"Time management could be improved\", \"Consider more advanced courses\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> weaknesses;
    
    @Schema(description = "Personalized recommendations for learning improvement", 
            example = "[\"Focus on JavaScript fundamentals\", \"Set consistent study schedule\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> recommendations;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Comprehensive performance metrics and learning statistics")
    public static class PerformanceMetrics {
        
        @Schema(description = "Total number of courses enrolled", 
                example = "8", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer totalCourses;
        
        @Schema(description = "Number of courses successfully completed", 
                example = "6", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer completedCourses;
        
        @Schema(description = "Course completion rate as a percentage", 
                example = "75.0", 
                minimum = "0", 
                maximum = "100",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double completionRate;
        
        @Schema(description = "Average score across all completed courses", 
                example = "87.5", 
                minimum = "0", 
                maximum = "100",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double averageScore;
        
        @Schema(description = "Total achievement points earned", 
                example = "1250", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer totalPoints;
        
        @Schema(description = "Current consecutive daily login streak", 
                example = "14", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer loginStreak;
        
        @Schema(description = "Hours spent learning this week", 
                example = "12", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer hoursThisWeek;
        
        @Schema(description = "Target hours per week set by user", 
                example = "15", 
                minimum = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer targetHoursPerWeek;
        
        @Schema(description = "Current badge level achieved", 
                example = "SILVER",
                allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String currentBadgeLevel;
    }
}