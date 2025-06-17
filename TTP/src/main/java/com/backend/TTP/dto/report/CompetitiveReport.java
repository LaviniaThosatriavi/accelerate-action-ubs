package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Competitive analysis report comparing user performance against peers and rankings")
public class CompetitiveReport {
    
    @Schema(description = "Type of report generated", 
            example = "COMPETITIVE_ANALYSIS",
            defaultValue = "COMPETITIVE_ANALYSIS",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String reportType = "COMPETITIVE_ANALYSIS";
    
    @Schema(description = "Executive summary of the user's competitive standing", 
            example = "You are in the top 25% of learners with strong consistency in daily learning",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String summary;
    
    @Schema(description = "Detailed competitive metrics and rankings", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private CompetitiveMetrics metrics;
    
    @Schema(description = "List of insights about user's competitive performance", 
            example = "[\"Your learning streak puts you ahead of 80% of users\", \"You've climbed 5 ranks this week\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> competitiveInsights;
    
    @Schema(description = "Motivational messages based on competitive analysis", 
            example = "[\"Keep up the great work! You're climbing the ranks\", \"Your consistency is paying off\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> motivationalMessages;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed competitive metrics and user rankings")
    public static class CompetitiveMetrics {
        
        @Schema(description = "Current rank among all users", 
                example = "142", 
                minimum = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer currentRank;
        
        @Schema(description = "Total number of users in the platform", 
                example = "1250", 
                minimum = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer totalUsers;
        
        @Schema(description = "Percentile ranking (higher is better)", 
                example = "75th percentile",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String percentile;
        
        @Schema(description = "Points earned this week", 
                example = "150", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer pointsThisWeek;
        
        @Schema(description = "Points needed to reach the next rank", 
                example = "25", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer pointsToNextRank;
        
        @Schema(description = "Direction of rank movement trend", 
                example = "Rising",
                allowableValues = {"Rising", "Falling", "Stable"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String trendDirection; // "Rising", "Falling", "Stable"
        
        @Schema(description = "Current badge level achieved", 
                example = "SILVER",
                allowableValues = {"BEGINNER", "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"},
                accessMode = Schema.AccessMode.READ_ONLY)
        private String badgeLevel;
        
        @Schema(description = "Points needed to reach the next badge level", 
                example = "350", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer pointsToNextBadge;
    }
}