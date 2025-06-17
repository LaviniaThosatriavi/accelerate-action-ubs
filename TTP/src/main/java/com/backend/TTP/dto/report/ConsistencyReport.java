package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Learning consistency analysis report tracking habits and goal completion patterns")
public class ConsistencyReport {
    
    @Schema(description = "Type of report generated", 
            example = "CONSISTENCY_ANALYSIS",
            defaultValue = "CONSISTENCY_ANALYSIS",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String reportType = "CONSISTENCY_ANALYSIS";
    
    @Schema(description = "Summary of the user's learning consistency patterns", 
            example = "Excellent consistency with daily login streak and 85% goal completion rate",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String summary;
    
    @Schema(description = "Detailed consistency metrics and tracking data", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private ConsistencyMetrics metrics;
    
    @Schema(description = "Insights about learning consistency patterns", 
            example = "[\"Your morning study sessions are most productive\", \"Weekend learning shows strong dedication\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> consistencyInsights;
    
    @Schema(description = "Suggestions for improving learning consistency", 
            example = "[\"Try setting consistent study times\", \"Use habit tracking tools\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> improvementSuggestions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed metrics for learning consistency and habit formation")
    public static class ConsistencyMetrics {
        
        @Schema(description = "Current consecutive daily login streak", 
                example = "14", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer currentLoginStreak;
        
        @Schema(description = "Longest consecutive login streak achieved", 
                example = "28", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer longestLoginStreak;
        
        @Schema(description = "Percentage of daily goals completed", 
                example = "85.5", 
                minimum = "0", 
                maximum = "100",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double goalCompletionRate;
        
        @Schema(description = "Number of goals completed this week", 
                example = "12", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer goalsCompletedThisWeek;
        
        @Schema(description = "Total number of goals set for this week", 
                example = "15", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer totalGoalsThisWeek;
        
        @Schema(description = "Overall consistency level assessment", 
                example = "Excellent",
                allowableValues = {"Excellent", "Good", "Fair", "Needs Improvement"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String consistencyLevel; // "Excellent", "Good", "Fair", "Needs Improvement"
        
        @Schema(description = "Average number of study sessions per week", 
                example = "6", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer averageStudySessionsPerWeek;
    }
}