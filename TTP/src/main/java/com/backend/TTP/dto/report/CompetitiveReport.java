package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitiveReport {
    private String reportType = "COMPETITIVE_ANALYSIS";
    private String summary;
    private CompetitiveMetrics metrics;
    private List<String> competitiveInsights;
    private List<String> motivationalMessages;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitiveMetrics {
        private Integer currentRank;
        private Integer totalUsers;
        private String percentile;
        private Integer pointsThisWeek;
        private Integer pointsToNextRank;
        private String trendDirection; // "Rising", "Falling", "Stable"
        private String badgeLevel;
        private Integer pointsToNextBadge;
    }
}
