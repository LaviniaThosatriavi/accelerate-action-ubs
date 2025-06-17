package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillAnalysisReport {
    private String reportType = "SKILL_ANALYSIS";
    private String summary;
    private List<SkillStrength> strongSkills;
    private List<SkillWeakness> weakSkills;
    private List<String> skillGaps;
    private List<String> recommendedSkills;
    private Map<String, Double> skillScores;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillStrength {
        private String skill;
        private Double averageScore;
        private Integer coursesCompleted;
        private String reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillWeakness {
        private String skill;
        private Double averageScore;
        private Integer coursesAttempted;
        private String reason;
        private String improvement;
    }
}
