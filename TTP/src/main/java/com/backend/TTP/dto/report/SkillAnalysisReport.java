package com.backend.TTP.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skill analysis report with strengths, weaknesses, and development recommendations")
public class SkillAnalysisReport {
    
    @Schema(description = "Type of report generated", 
            example = "SKILL_ANALYSIS",
            defaultValue = "SKILL_ANALYSIS",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String reportType = "SKILL_ANALYSIS";
    
    @Schema(description = "Summary of the user's skill development and proficiency", 
            example = "Strong foundation in frontend technologies with opportunities to develop backend skills",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String summary;
    
    @Schema(description = "List of user's strongest skills with performance data", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<SkillStrength> strongSkills;
    
    @Schema(description = "List of skills needing improvement with analysis", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<SkillWeakness> weakSkills;
    
    @Schema(description = "Identified gaps in the user's skill set", 
            example = "[\"Backend development\", \"Database design\", \"Testing frameworks\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> skillGaps;
    
    @Schema(description = "Skills recommended for learning based on career goals", 
            example = "[\"Node.js\", \"MongoDB\", \"Jest testing\"]",
            accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> recommendedSkills;
    
    @Schema(description = "Map of skill names to their proficiency scores", 
            example = "{\"JavaScript\": 85.5, \"React\": 78.2, \"CSS\": 92.1}",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, Double> skillScores;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Analysis of a user's skill strength with performance metrics")
    public static class SkillStrength {
        
        @Schema(description = "Name of the skill", 
                example = "JavaScript",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String skill;
        
        @Schema(description = "Average score achieved in this skill area", 
                example = "87.5", 
                minimum = "0", 
                maximum = "100",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double averageScore;
        
        @Schema(description = "Number of courses completed in this skill area", 
                example = "4", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer coursesCompleted;
        
        @Schema(description = "Explanation of why this is considered a strength", 
                example = "Consistently high scores and rapid progress in JavaScript fundamentals",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Analysis of a skill that needs improvement with development suggestions")
    public static class SkillWeakness {
        
        @Schema(description = "Name of the skill needing improvement", 
                example = "Database Design",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String skill;
        
        @Schema(description = "Average score in this skill area", 
                example = "65.2", 
                minimum = "0", 
                maximum = "100",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Double averageScore;
        
        @Schema(description = "Number of courses attempted in this skill area", 
                example = "2", 
                minimum = "0",
                accessMode = Schema.AccessMode.READ_ONLY)
        private Integer coursesAttempted;
        
        @Schema(description = "Analysis of why this skill needs improvement", 
                example = "Lower completion rates and scores indicate need for more foundational work",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String reason;
        
        @Schema(description = "Specific suggestions for improvement", 
                example = "Start with SQL fundamentals before advancing to database architecture",
                accessMode = Schema.AccessMode.READ_ONLY)
        private String improvement;
    }
}