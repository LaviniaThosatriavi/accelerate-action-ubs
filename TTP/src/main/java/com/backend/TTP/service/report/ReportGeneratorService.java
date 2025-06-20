package com.backend.TTP.service.report;

import com.backend.TTP.dto.LeaderboardResponse;
import com.backend.TTP.dto.LeaderboardUserDTO;
import com.backend.TTP.dto.report.*;
import com.backend.TTP.model.*;
import com.backend.TTP.repository.*;
import com.backend.TTP.service.AchievementService;
import com.backend.TTP.service.EnrolledCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportGeneratorService {
    
    @Autowired
    private ReportAnalyticsService analyticsService;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrolledCourseService enrolledCourseService;
    
    public LearningOverviewReport generateOverviewReport(User user) {
        Map<String, Object> basicMetrics = analyticsService.getBasicMetrics(user);
        Map<String, Double> skillScores = analyticsService.getSkillScores(user);
        Map<String, Object> goalMetrics = analyticsService.getGoalMetrics(user);
        
        // Create performance metrics
        LearningOverviewReport.PerformanceMetrics performance = new LearningOverviewReport.PerformanceMetrics();
        performance.setTotalCourses((Integer) basicMetrics.get("totalCourses"));
        performance.setCompletedCourses((Integer) basicMetrics.get("completedCourses"));
        performance.setCompletionRate((Double) basicMetrics.get("completionRate"));
        performance.setAverageScore((Double) basicMetrics.get("averageScore"));
        performance.setTotalPoints((Integer) basicMetrics.get("totalPoints"));
        performance.setLoginStreak((Integer) basicMetrics.get("loginStreak"));
        performance.setHoursThisWeek((Integer) basicMetrics.get("hoursThisWeek"));
        performance.setTargetHoursPerWeek((Integer) basicMetrics.get("targetHoursPerWeek"));
        performance.setCurrentBadgeLevel((String) basicMetrics.get("currentBadgeLevel"));
        
        // Generate insights
        List<String> strengths = analyticsService.identifyStrengths(basicMetrics, skillScores);
        List<String> weaknesses = analyticsService.identifyWeaknesses(basicMetrics, skillScores);
        List<String> recommendations = analyticsService.generateRecommendations(basicMetrics, skillScores, goalMetrics);
        
        // Create summary
        String summary = generateOverviewSummary(performance, strengths, weaknesses);
        
        LearningOverviewReport report = new LearningOverviewReport();
        report.setSummary(summary);
        report.setPerformance(performance);
        report.setStrengths(strengths);
        report.setWeaknesses(weaknesses);
        report.setRecommendations(recommendations);
        
        return report;
    }
    
    public SkillAnalysisReport generateSkillAnalysisReport(User user) {
        Map<String, Double> skillScores = analyticsService.getSkillScores(user);
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        
        // Identify strong skills (>75%)
        List<SkillAnalysisReport.SkillStrength> strongSkills = skillScores.entrySet().stream()
            .filter(entry -> entry.getValue() >= 75.0)
            .map(entry -> new SkillAnalysisReport.SkillStrength(
                entry.getKey(),
                entry.getValue(),
                getCoursesCountForSkill(entry.getKey(), user),
                "Consistently high performance"
            ))
            .collect(Collectors.toList());
        
        // Identify weak skills (<65%)
        List<SkillAnalysisReport.SkillWeakness> weakSkills = skillScores.entrySet().stream()
            .filter(entry -> entry.getValue() < 65.0)
            .map(entry -> new SkillAnalysisReport.SkillWeakness(
                entry.getKey(),
                entry.getValue(),
                getCoursesCountForSkill(entry.getKey(), user),
                "Below target performance",
                "Focus on foundational concepts and practice"
            ))
            .collect(Collectors.toList());
        
        // Identify skill gaps
        List<String> skillGaps = identifySkillGaps(profile, skillScores);
        
        // Generate recommendations
        List<String> recommendedSkills = generateSkillRecommendations(skillScores, profile);
        
        String summary = generateSkillSummary(strongSkills, weakSkills, skillGaps);
        
        SkillAnalysisReport report = new SkillAnalysisReport();
        report.setSummary(summary);
        report.setStrongSkills(strongSkills);
        report.setWeakSkills(weakSkills);
        report.setSkillGaps(skillGaps);
        report.setRecommendedSkills(recommendedSkills);
        report.setSkillScores(skillScores);
        
        return report;
    }
    
    public ConsistencyReport generateConsistencyReport(User user) {
        Map<String, Object> basicMetrics = analyticsService.getBasicMetrics(user);
        Map<String, Object> goalMetrics = analyticsService.getGoalMetrics(user);
        
        // Create consistency metrics
        ConsistencyReport.ConsistencyMetrics metrics = new ConsistencyReport.ConsistencyMetrics();
        metrics.setCurrentLoginStreak((Integer) basicMetrics.get("loginStreak"));
        metrics.setLongestLoginStreak(getLongestLoginStreak(user));
        metrics.setGoalCompletionRate((Double) goalMetrics.get("goalCompletionRate"));
        metrics.setGoalsCompletedThisWeek((Integer) goalMetrics.get("completedGoalsThisWeek"));
        metrics.setTotalGoalsThisWeek((Integer) goalMetrics.get("totalGoalsThisWeek"));
        metrics.setAverageStudySessionsPerWeek(0); // Removed as requested
        
        // Determine consistency level
        String consistencyLevel = determineConsistencyLevel(metrics);
        metrics.setConsistencyLevel(consistencyLevel);
        
        // Generate insights and suggestions
        List<String> insights = generateConsistencyInsights(metrics);
        List<String> suggestions = generateConsistencyImprovements(metrics);
        
        String summary = generateConsistencySummary(metrics);
        
        ConsistencyReport report = new ConsistencyReport();
        report.setSummary(summary);
        report.setMetrics(metrics);
        report.setConsistencyInsights(insights);
        report.setImprovementSuggestions(suggestions);
        
        return report;
    }
    
    public TimeManagementReport generateTimeManagementReport(User user) {
        Map<String, Object> basicMetrics = analyticsService.getBasicMetrics(user);
        
        // Get actual hours from the new API endpoint
        Integer actualHours = enrolledCourseService.getTotalHoursThisWeek(user);
        Integer plannedHours = (Integer) basicMetrics.get("targetHoursPerWeek");
        
        TimeManagementReport.TimeAnalysis analysis = new TimeManagementReport.TimeAnalysis();
        analysis.setPlannedHoursPerWeek(plannedHours);
        
        // Calculate utilization rate
        double utilizationRate = plannedHours > 0 ? (actualHours * 100.0 / plannedHours) : 0.0;
        analysis.setTimeUtilizationRate(Math.round(utilizationRate * 10.0) / 10.0);
        
        // Generate recommendations
        String recommendation = determineTimeRecommendation(actualHours, plannedHours);
        analysis.setRecommendation(recommendation);
        
        Integer optimalHours = calculateOptimalHours(user, basicMetrics);
        analysis.setOptimalHoursPerWeek(optimalHours);
        
        String learningPace = determineLearningPace(basicMetrics);
        analysis.setLearningPace(learningPace);
        
        analysis.setHasOverdueDeadlines(checkOverdueDeadlines(user));
        
        List<String> insights = generateTimeInsights(analysis, actualHours);
        List<String> tips = generateOptimizationTips(analysis);
        
        String summary = generateTimeSummary(analysis, actualHours);
        
        TimeManagementReport report = new TimeManagementReport();
        report.setSummary(summary);
        report.setTimeAnalysis(analysis);
        report.setTimeInsights(insights);
        report.setOptimizationTips(tips);
        
        return report;
    }
    
    public CompetitiveReport generateCompetitiveReport(User user) {
        Map<String, Object> competitiveMetrics = analyticsService.getCompetitiveMetrics(user);
        Map<String, Object> basicMetrics = analyticsService.getBasicMetrics(user);
        
        CompetitiveReport.CompetitiveMetrics metrics = new CompetitiveReport.CompetitiveMetrics();
        metrics.setCurrentRank((Integer) competitiveMetrics.get("currentRank"));
        metrics.setTotalUsers(getTotalActiveUsers());
        
        // Calculate percentile
        Integer rank = (Integer) competitiveMetrics.get("currentRank");
        String percentile = calculatePercentile(rank, metrics.getTotalUsers());
        metrics.setPercentile(percentile);
        
        metrics.setPointsThisWeek((Integer) competitiveMetrics.get("dailyPoints"));
        metrics.setPointsToNextRank(calculatePointsToNextRank(user, rank));
        metrics.setTrendDirection("Stable"); 
        metrics.setBadgeLevel((String) basicMetrics.get("currentBadgeLevel"));
        metrics.setPointsToNextBadge(calculatePointsToNextBadge(basicMetrics));
        
        List<String> insights = generateCompetitiveInsights(metrics);
        List<String> motivationalMessages = generateMotivationalMessages(metrics);
        
        String summary = generateCompetitiveSummary(metrics);
        
        CompetitiveReport report = new CompetitiveReport();
        report.setSummary(summary);
        report.setMetrics(metrics);
        report.setCompetitiveInsights(insights);
        report.setMotivationalMessages(motivationalMessages);
        
        return report;
    }
    
    public ComprehensiveReport generateComprehensiveReport(User user) {
        ComprehensiveReport report = new ComprehensiveReport();
        
        report.setUserId(user.getId().toString());
        report.setUsername(user.getUsername());
        report.setGeneratedAt(LocalDateTime.now());
        
        // Generate all sub-reports
        LearningOverviewReport overview = generateOverviewReport(user);
        SkillAnalysisReport skillAnalysis = generateSkillAnalysisReport(user);
        ConsistencyReport consistency = generateConsistencyReport(user);
        TimeManagementReport timeManagement = generateTimeManagementReport(user);
        CompetitiveReport competitive = generateCompetitiveReport(user);
        
        report.setOverview(overview);
        report.setSkillAnalysis(skillAnalysis);
        report.setConsistency(consistency);
        report.setTimeManagement(timeManagement);
        report.setCompetitive(competitive);
        
        // Generate executive summary
        String executiveSummary = generateExecutiveSummary(overview, skillAnalysis, consistency);
        report.setExecutiveSummary(executiveSummary);
        
        // Compile key recommendations
        List<String> keyRecommendations = compileKeyRecommendations(
            overview.getRecommendations(),
            skillAnalysis.getRecommendedSkills(),
            consistency.getImprovementSuggestions(),
            timeManagement.getOptimizationTips()
        );
        report.setKeyRecommendations(keyRecommendations);
        
        // Generate immediate actions
        List<String> immediateActions = generateImmediateActions(overview, timeManagement);
        report.setImmediateActions(immediateActions);
        
        return report;
    }
    
    // Helper methods for calculations and text generation
    
    private String generateOverviewSummary(LearningOverviewReport.PerformanceMetrics performance, 
                                         List<String> strengths, List<String> weaknesses) {
        return String.format("You have completed %d out of %d courses (%.1f%% completion rate) with an average score of %.1f%%. " +
            "Your current badge level is %s with %d total points and a %d-day login streak.",
            performance.getCompletedCourses(), performance.getTotalCourses(), 
            performance.getCompletionRate(), performance.getAverageScore(),
            performance.getCurrentBadgeLevel(), performance.getTotalPoints(), performance.getLoginStreak());
    }
    
    private String generateSkillSummary(List<SkillAnalysisReport.SkillStrength> strongSkills, 
                                      List<SkillAnalysisReport.SkillWeakness> weakSkills, 
                                      List<String> skillGaps) {
        return String.format("You excel in %d skill areas, have %d skills needing improvement, and %d skill gaps to address.",
            strongSkills.size(), weakSkills.size(), skillGaps.size());
    }
    
    private String generateConsistencySummary(ConsistencyReport.ConsistencyMetrics metrics) {
        return String.format("Your consistency level is %s with a %d-day login streak and %.1f%% goal completion rate.",
            metrics.getConsistencyLevel(), metrics.getCurrentLoginStreak(), metrics.getGoalCompletionRate());
    }
    
    private String generateTimeSummary(TimeManagementReport.TimeAnalysis analysis, Integer actualHours) {
        return String.format("You've studied %d hours this week out of %d planned (%.1f%% utilization). Recommendation: %s your study time.",
            actualHours, analysis.getPlannedHoursPerWeek(), 
            analysis.getTimeUtilizationRate(), analysis.getRecommendation());
    }
    
    private String generateCompetitiveSummary(CompetitiveReport.CompetitiveMetrics metrics) {
        return String.format("You're ranked #%d (%s percentile) with %s badge level. You earned %d points this week.",
            metrics.getCurrentRank(), metrics.getPercentile(), 
            metrics.getBadgeLevel(), metrics.getPointsThisWeek());
    }
    
    private Integer getCoursesCountForSkill(String skill, User user) {
        // Get all completed courses for the user
        List<EnrolledCourse> completedCourses = enrolledCourseRepository.findByUserAndStatus(user, "COMPLETED");
        
        // Count courses that match the skill category
        return (int) completedCourses.stream()
            .filter(enrolledCourse -> {
                Course course = enrolledCourse.getCourse();
                return course != null && skill.equals(course.getCategory());
            })
            .count();
    }
    
    private List<String> identifySkillGaps(UserProfile profile, Map<String, Double> skillScores) {
        List<String> gaps = new ArrayList<>();
        if (profile != null && profile.getSkills() != null) {
            for (String profileSkill : profile.getSkills()) {
                if (!skillScores.containsKey(profileSkill)) {
                    gaps.add(profileSkill + " - no courses completed yet");
                }
            }
        }
        return gaps;
    }
    
    private List<String> generateSkillRecommendations(Map<String, Double> skillScores, UserProfile profile) {
        List<String> recommendations = new ArrayList<>();
        
        // Recommend improving weak skills
        skillScores.entrySet().stream()
            .filter(entry -> entry.getValue() < 70.0)
            .forEach(entry -> recommendations.add("Take advanced " + entry.getKey() + " courses"));
        
        // Recommend related skills
        if (skillScores.containsKey("Programming")) {
            recommendations.add("Consider learning Database Management");
        }
        if (skillScores.containsKey("Data Science")) {
            recommendations.add("Consider learning Machine Learning");
        }
        
        return recommendations.isEmpty() ? 
            Arrays.asList("Continue building on your strong foundation") : recommendations;
    }
    
    private Integer getLongestLoginStreak(User user) {
        // Since we only track current streak, return current streak
        // Could be enhanced by tracking historical streak data
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        return profile != null ? profile.getLoginStreak() : 0;
    }
    
    private String determineConsistencyLevel(ConsistencyReport.ConsistencyMetrics metrics) {
        double goalRate = metrics.getGoalCompletionRate();
        int streak = metrics.getCurrentLoginStreak();
        
        if (goalRate >= 80 && streak >= 10) return "Excellent";
        if (goalRate >= 60 && streak >= 5) return "Good";
        if (goalRate >= 40 && streak >= 2) return "Fair";
        return "Needs Improvement";
    }
    
    private List<String> generateConsistencyInsights(ConsistencyReport.ConsistencyMetrics metrics) {
        List<String> insights = new ArrayList<>();
        
        if (metrics.getCurrentLoginStreak() >= 7) {
            insights.add("You have an excellent daily learning habit");
        }
        
        if (metrics.getGoalCompletionRate() >= 75) {
            insights.add("You consistently achieve your daily goals");
        } else {
            insights.add("Your goal completion could be improved with better planning");
        }
        
        return insights;
    }
    
    private List<String> generateConsistencyImprovements(ConsistencyReport.ConsistencyMetrics metrics) {
        List<String> suggestions = new ArrayList<>();
        
        if (metrics.getCurrentLoginStreak() < 3) {
            suggestions.add("Set a daily reminder to maintain learning momentum");
        }
        
        if (metrics.getGoalCompletionRate() < 60) {
            suggestions.add("Break down goals into smaller, manageable tasks");
            suggestions.add("Set realistic daily targets based on available time");
        }
        
        return suggestions;
    }
    
    private String determineTimeRecommendation(Integer actualHours, Integer plannedHours) {
        if (actualHours < plannedHours * 0.7) return "increase";
        if (actualHours > plannedHours * 1.3) return "decrease";
        return "maintain";
    }
    
    private Integer calculateOptimalHours(User user, Map<String, Object> basicMetrics) {
        Double completionRate = (Double) basicMetrics.get("completionRate");
        Integer currentTarget = (Integer) basicMetrics.get("targetHoursPerWeek");
        
        // Base recommendation on completion rate and current performance
        if (completionRate >= 80.0) {
            return Math.min(currentTarget + 2, 15); // Can handle more
        } else if (completionRate < 50.0) {
            return Math.max(currentTarget - 2, 3); // Should reduce load
        }
        return currentTarget; // Current target is appropriate
    }
    
    private String determineLearningPace(Map<String, Object> basicMetrics) {
        Double completionRate = (Double) basicMetrics.get("completionRate");
        if (completionRate >= 80) return "Fast";
        if (completionRate >= 50) return "Moderate";
        return "Slow";
    }
    
    private Boolean checkOverdueDeadlines(User user) {
        LocalDate today = LocalDate.now();
        
        // Get all enrolled courses that are not completed
        List<EnrolledCourse> activeCourses = enrolledCourseRepository.findByUser(user).stream()
            .filter(course -> !"COMPLETED".equals(course.getStatus()))
            .collect(Collectors.toList());
        
        // Check if any have target completion dates in the past
        return activeCourses.stream()
            .anyMatch(course -> course.getTargetCompletionDate() != null && 
                               course.getTargetCompletionDate().isBefore(today));
    }
    
    private List<String> generateTimeInsights(TimeManagementReport.TimeAnalysis analysis, Integer actualHours) {
        List<String> insights = new ArrayList<>();
        
        if (analysis.getTimeUtilizationRate() < 70) {
            insights.add("You're not utilizing your planned study time effectively");
        } else if (analysis.getTimeUtilizationRate() > 120) {
            insights.add("You're exceeding your planned study time - great dedication!");
        }
        
        insights.add("Your learning pace is " + analysis.getLearningPace().toLowerCase());
        
        if (analysis.getHasOverdueDeadlines()) {
            insights.add("You have overdue course deadlines that need attention");
        }
        
        return insights;
    }
    
    private List<String> generateOptimizationTips(TimeManagementReport.TimeAnalysis analysis) {
        List<String> tips = new ArrayList<>();
        
        if ("increase".equals(analysis.getRecommendation())) {
            tips.add("Schedule specific time blocks for learning");
            tips.add("Use the Pomodoro technique for focused study sessions");
        } else if ("decrease".equals(analysis.getRecommendation())) {
            tips.add("Ensure you're taking adequate breaks to avoid burnout");
            tips.add("Quality over quantity - focus on understanding concepts");
        }
        
        tips.add("Track your most productive hours and schedule important topics then");
        
        if (analysis.getHasOverdueDeadlines()) {
            tips.add("Prioritize overdue courses to get back on track");
        }
        
        return tips;
    }
    
    private Integer getTotalActiveUsers() {
        return userProfileRepository.findAll().size();
    }
    
    private String calculatePercentile(Integer rank, Integer totalUsers) {
        if (rank == null || totalUsers == null || totalUsers == 0) return "N/A";
        double percentile = ((totalUsers - rank + 1.0) / totalUsers) * 100;
        return String.format("Top %.0f%%", percentile);
    }
    
    private Integer calculatePointsToNextRank(User user, Integer currentRank) {
        // Get the user just above current user in ranking
        if (currentRank <= 1) return 0; // Already at top
        
        try {
            LeaderboardResponse leaderboard = achievementService.getLeaderboard(user, "daily", currentRank);
            if (leaderboard.getTopUsers().size() >= currentRank - 1) {
                LeaderboardUserDTO userAbove = leaderboard.getTopUsers().get(currentRank - 2);
                LeaderboardUserDTO currentUser = leaderboard.getCurrentUser();
                if (currentUser != null) {
                    return Math.max(0, userAbove.getPoints() - currentUser.getPoints() + 1);
                }
            }
        } catch (Exception e) {
            // Fallback calculation
        }
        return 10; // Default estimate
    }
    
    private Integer calculatePointsToNextBadge(Map<String, Object> basicMetrics) {
        String currentBadge = (String) basicMetrics.get("currentBadgeLevel");
        Integer totalPoints = (Integer) basicMetrics.get("totalPoints");
        
        // Badge thresholds from AchievementService
        return switch (currentBadge) {
            case "NOVICE" -> Math.max(0, 100 - totalPoints);
            case "APPRENTICE" -> Math.max(0, 300 - totalPoints);
            case "SCHOLAR" -> Math.max(0, 700 - totalPoints);
            case "EXPERT" -> Math.max(0, 1500 - totalPoints);
            case "MASTER" -> 0; // Already at max level
            default -> 100; // Default to APPRENTICE threshold
        };
    }
    
    private List<String> generateCompetitiveInsights(CompetitiveReport.CompetitiveMetrics metrics) {
        List<String> insights = new ArrayList<>();
        
        // Badge level insights
        String badgeInsight = switch (metrics.getBadgeLevel()) {
            case "NOVICE" -> "You're just starting your learning journey - great first steps!";
            case "APPRENTICE" -> "You're building momentum as a Knowledge Seeker!";
            case "SCHOLAR" -> "Excellent progress as a Dedicated Learner!";
            case "EXPERT" -> "Outstanding achievement as a Skill Master!";
            case "MASTER" -> "Congratulations! You're a Learning Champion at the highest level!";
            default -> "Keep learning to advance your badge level!";
        };
        insights.add(badgeInsight);
        
        // Ranking insights with your additions
        if (metrics.getCurrentRank() <= 3) {
            insights.add("You're in the top 3 - EXCELLENT PERFORMANCE!");
        } else if (metrics.getCurrentRank() <= 10) {
            insights.add("You're in the top 10 - keep up the great work!");
        } else if (metrics.getCurrentRank() <= 25) {
            insights.add("You're performing well in the top quartile");
        } else if (metrics.getCurrentRank() <= 50) {
            insights.add("You're in the top half - keep pushing forward!");
        }
        
        if (metrics.getPointsThisWeek() > 0) {
            insights.add("You earned " + metrics.getPointsThisWeek() + " points this week");
        }
        
        return insights;
    }
    
    private List<String> generateMotivationalMessages(CompetitiveReport.CompetitiveMetrics metrics) {
        List<String> messages = new ArrayList<>();
        
        // Check if user is MASTER with maximum achievement
        boolean isMasterAtTop = "MASTER".equals(metrics.getBadgeLevel()) && 
                               metrics.getPointsToNextBadge() == 0 && 
                               metrics.getCurrentRank() <= 3;
        
        if (isMasterAtTop) {
            // Special message for top Masters
            if (metrics.getCurrentRank() == 1) {
                messages.add("Excellent work maintaining your #1 position - you're the ultimate learning champion!");
            } else {
                messages.add("Outstanding performance! Keep defending your top " + metrics.getCurrentRank() + " position!");
            }
        } else {
            // Standard momentum message for all other users
            messages.add("Keep up the momentum - every point counts!");
        }
        
        if (metrics.getPointsToNextBadge() > 0) {
            String nextBadge = getNextBadgeName(metrics.getBadgeLevel());
            messages.add("You're " + metrics.getPointsToNextBadge() + " points away from " + nextBadge + " level!");
        } else {
            if (!isMasterAtTop) {
                messages.add("Congratulations! You've reached the highest badge level - MASTER!");
            }
        }
        
        if (metrics.getCurrentRank() > 50) {
            messages.add("Challenge yourself to climb the leaderboard this week");
        } else if (metrics.getCurrentRank() <= 10) {
            messages.add("Amazing! You're in the top 10 learners!");
        }
        
        return messages;
    }
    
    private String getNextBadgeName(String currentBadge) {
        return switch (currentBadge) {
            case "NOVICE" -> "APPRENTICE";
            case "APPRENTICE" -> "SCHOLAR"; 
            case "SCHOLAR" -> "EXPERT";
            case "EXPERT" -> "MASTER";
            case "MASTER" -> "MASTER";
            default -> "APPRENTICE";
        };
    }
    
    private String generateExecutiveSummary(LearningOverviewReport overview, 
                                          SkillAnalysisReport skillAnalysis, 
                                          ConsistencyReport consistency) {
        return String.format("Your learning journey shows %s consistency with %d completed courses. " +
            "You excel in %d skill areas and maintain a strong learning rhythm. " +
            "Focus on addressing skill gaps and maintaining your %d-day streak.",
            consistency.getMetrics().getConsistencyLevel().toLowerCase(),
            overview.getPerformance().getCompletedCourses(),
            skillAnalysis.getStrongSkills().size(),
            consistency.getMetrics().getCurrentLoginStreak());
    }
    
    private List<String> compileKeyRecommendations(List<String>... recommendationLists) {
        List<String> compiled = new ArrayList<>();
        for (List<String> recommendations : recommendationLists) {
            if (recommendations != null) {
                compiled.addAll(recommendations.stream().limit(2).collect(Collectors.toList()));
            }
        }
        return compiled.stream().limit(5).collect(Collectors.toList());
    }
    
    private List<String> generateImmediateActions(LearningOverviewReport overview, 
                                                TimeManagementReport timeManagement) {
        List<String> actions = new ArrayList<>();
        
        if (timeManagement.getTimeAnalysis().getTimeUtilizationRate() < 70) {
            actions.add("Schedule 30 minutes of focused study time today");
        }
        
        if (overview.getPerformance().getCompletionRate() < 50) {
            actions.add("Complete one enrolled course this week");
        }
        
        actions.add("Set 2-3 achievable goals for tomorrow");
        actions.add("Review and practice your weakest skill area");
        
        return actions;
    }
}