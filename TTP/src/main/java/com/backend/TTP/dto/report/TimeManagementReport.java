package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Time management analysis report with productivity insights and optimization recommendations")
public class TimeManagementReport {
    
    @Schema(description = "Type of report generated", 
            example = "TIME_MANAGEMENT",
            defaultValue = "TIME_MANAGEMENT",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String reportType = "TIME_MANAGEMENT";
    
    @Schema(description = "Summary of the user's time management and learning efficiency", 
            example = "Good time utilization with opportunity to optimize study schedule for better productivity",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String summary;
    
    @Schema(description = "Detailed time analysis and utilization metrics", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private TimeAnalysis timeAnalysis;
    
    @Schema(description = "Insights about time management patterns and productivity", 
            example = "[\"Most productive during morning hours\", \"Longer study sessions show better retention\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> timeInsights;
    
    @Schema(description = "Tips for optimizing time management and study efficiency", 
            example = "[\"Schedule difficult topics during peak energy hours\", \"Take breaks every 45 minutes\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> optimizationTips;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed time analysis metrics and productivity measurements")
    public static class TimeAnalysis {
        
        @Schema(description = "Target hours per week set by the user", 
                example = "15", 
                minimum = "1", 
                maximum = "168",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer plannedHoursPerWeek;
        
        @Schema(description = "Actual hours spent learning this week", 
                example = "12", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer actualHoursThisWeek;
        
        @Schema(description = "Percentage of planned time actually utilized", 
                example = "80.0", 
                minimum = "0", 
                maximum = "200",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double timeUtilizationRate;
        
        @Schema(description = "Recommendation for time allocation adjustment", 
                example = "maintain",
                allowableValues = {"increase", "decrease", "maintain"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String recommendation; // "increase", "decrease", "maintain"
        
        @Schema(description = "AI-suggested optimal hours per week for this user", 
                example = "18", 
                minimum = "1", 
                maximum = "168",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer optimalHoursPerWeek;
        
        @Schema(description = "Assessment of the user's learning pace", 
                example = "Moderate",
                allowableValues = {"Fast", "Moderate", "Slow"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String learningPace; // "Fast", "Moderate", "Slow"
        
        @Schema(description = "Whether the user has any overdue deadlines", 
                example = "false",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Boolean hasOverdueDeadlines;
    }
}