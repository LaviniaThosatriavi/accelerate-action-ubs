package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comprehensive learning analytics report combining all analysis types")
public class ComprehensiveReport {
    
    @Schema(description = "User ID for whom the report is generated", 
            example = "user123",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String userId;
    
    @Schema(description = "Username of the user", 
            example = "john_doe",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    
    @Schema(description = "Timestamp when the report was generated", 
            example = "2024-06-15T14:30:00",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime generatedAt;
    
    @Schema(description = "High-level executive summary of the user's learning journey", 
            example = "Strong learner with consistent daily habits and excellent course completion rate",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String executiveSummary;
    
    @Schema(description = "Learning overview analysis with performance metrics", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private LearningOverviewReport overview;
    
    @Schema(description = "Detailed skill analysis and development tracking", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private SkillAnalysisReport skillAnalysis;
    
    @Schema(description = "Learning consistency and habit formation analysis", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private ConsistencyReport consistency;
    
    @Schema(description = "Time management and productivity analysis", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private TimeManagementReport timeManagement;
    
    @Schema(description = "Competitive analysis and peer comparison", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private CompetitiveReport competitive;
    
    @Schema(description = "Top strategic recommendations for improvement", 
            example = "[\"Focus on completing JavaScript courses\", \"Increase daily study time to 2 hours\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> keyRecommendations;
    
    @Schema(description = "Immediate actionable steps the user can take", 
            example = "[\"Complete the pending React tutorial\", \"Set up daily study reminders\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> immediateActions;
}