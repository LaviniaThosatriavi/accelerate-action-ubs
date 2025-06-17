package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsistencyReport {
    private String reportType = "CONSISTENCY_ANALYSIS";
    private String summary;
    private ConsistencyMetrics metrics;
    private List<String> consistencyInsights;
    private List<String> improvementSuggestions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsistencyMetrics {
        private Integer currentLoginStreak;
        private Integer longestLoginStreak;
        private Double goalCompletionRate;
        private Integer goalsCompletedThisWeek;
        private Integer totalGoalsThisWeek;
        private String consistencyLevel; // "Excellent", "Good", "Fair", "Needs Improvement"
        private Integer averageStudySessionsPerWeek;
    }
}