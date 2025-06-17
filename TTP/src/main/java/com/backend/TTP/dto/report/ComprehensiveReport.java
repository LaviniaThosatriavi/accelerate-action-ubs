package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveReport {
    private String userId;
    private String username;
    private LocalDateTime generatedAt;
    private String executiveSummary;
    private LearningOverviewReport overview;
    private SkillAnalysisReport skillAnalysis;
    private ConsistencyReport consistency;
    private TimeManagementReport timeManagement;
    private CompetitiveReport competitive;
    private List<String> keyRecommendations;
    private List<String> immediateActions;
}