package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningOverviewReport {
    private String reportType = "LEARNING_OVERVIEW";
    private String summary;
    private PerformanceMetrics performance;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private Integer totalCourses;
        private Integer completedCourses;
        private Double completionRate;
        private Double averageScore;
        private Integer totalPoints;
        private Integer loginStreak;
        private Integer hoursThisWeek;
        private Integer targetHoursPerWeek;
        private String currentBadgeLevel;
    }
}
