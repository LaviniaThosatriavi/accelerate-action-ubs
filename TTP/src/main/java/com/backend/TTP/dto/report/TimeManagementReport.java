package com.backend.TTP.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeManagementReport {
    private String reportType = "TIME_MANAGEMENT";
    private String summary;
    private TimeAnalysis timeAnalysis;
    private List<String> timeInsights;
    private List<String> optimizationTips;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeAnalysis {
        private Integer plannedHoursPerWeek;
        private Integer actualHoursThisWeek;
        private Double timeUtilizationRate;
        private String recommendation; // "increase", "decrease", "maintain"
        private Integer optimalHoursPerWeek;
        private String learningPace; // "Fast", "Moderate", "Slow"
        private Boolean hasOverdueDeadlines;
    }
}