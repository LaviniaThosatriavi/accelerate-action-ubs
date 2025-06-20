package com.backend.TTP.service.report;

import com.backend.TTP.model.*;
import com.backend.TTP.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class ReportAnalyticsService {
    
    @Autowired
    private CourseScoreRepository courseScoreRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    @Autowired
    private DailyGoalRepository dailyGoalRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private LeaderboardEntryRepository leaderboardEntryRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    public Map<String, Object> getBasicMetrics(User user) {
        Map<String, Object> metrics = new HashMap<>();
        
        List<CourseScore> scores = courseScoreRepository.findByUser(user);
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUser(user);
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        
        // Course completion metrics
        int totalCourses = enrolledCourses.size();
        int completedCourses = scores.size();
        double completionRate = totalCourses > 0 ? (completedCourses * 100.0 / totalCourses) : 0.0;
        
        // Score metrics
        Double averageScore = scores.stream()
            .mapToDouble(CourseScore::getPercentage)
            .average().orElse(0.0);
        
        // Time metrics
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        
        Double hoursThisWeek = dailyGoalRepository.sumAllocatedHoursByUserIdAndDateRange(
            user.getId(), weekStart, weekEnd);
        
        metrics.put("totalCourses", totalCourses);
        metrics.put("completedCourses", completedCourses);
        metrics.put("completionRate", Math.round(completionRate * 10.0) / 10.0);
        metrics.put("averageScore", Math.round(averageScore * 10.0) / 10.0);
        metrics.put("hoursThisWeek", hoursThisWeek != null ? hoursThisWeek.intValue() : 0);
        metrics.put("targetHoursPerWeek", profile != null ? profile.getHoursPerWeek() : 0);
        metrics.put("loginStreak", profile != null ? profile.getLoginStreak() : 0);
        metrics.put("totalPoints", profile != null ? profile.getTotalPoints() : 0);
        metrics.put("currentBadgeLevel", profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
        
        return metrics;
    }
    
    public Map<String, Double> getSkillScores(User user) {
        List<CourseScore> scores = courseScoreRepository.findByUser(user);
        Map<String, List<Double>> skillScoreMap = new HashMap<>();

        for (CourseScore score : scores) {
            Course course = courseRepository.findById(score.getCourseId()).orElse(null);
            if (course != null) {
                String skillName = extractSkillName(course);
                skillScoreMap.computeIfAbsent(skillName, k -> new ArrayList<>())
                    .add(score.getPercentage());
            }
        }

        Map<String, Double> avgSkillScores = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : skillScoreMap.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            avgSkillScores.put(entry.getKey(), Math.round(avg * 10.0) / 10.0);
        }

        return avgSkillScores;
    }

    /**
     * Extract meaningful skill name from course data
     * Priority: tags -> title -> category
     */
    private String extractSkillName(Course course) {
    // First, try to get skill from tags (if any meaningful tags exist)
    if (course.getTags() != null && !course.getTags().isEmpty()) {
        // Look for skill-related tags
        Set<String> tags = course.getTags();
        for (String tag : tags) {
            String cleanTag = tag.toLowerCase().trim();
            // Skip generic tags and use specific skill tags
            if (!isGenericTag(cleanTag)) {
                return formatSkillName(tag);
            }
        }
    }

    // If no meaningful tags, use course title
    if (course.getTitle() != null && !course.getTitle().trim().isEmpty()) {
        return formatSkillName(course.getTitle());
    }

    // Fallback to category if everything else is null
    if (course.getCategory() != null && !course.getCategory().trim().isEmpty()) {
        // If category is generic like "EXTERNAL", use course title instead
        if ("EXTERNAL".equalsIgnoreCase(course.getCategory()) || 
            "INTERNAL".equalsIgnoreCase(course.getCategory())) {
            return course.getTitle() != null ? 
                formatSkillName(course.getTitle()) : 
                course.getCategory() + " Course";
        }
        return formatSkillName(course.getCategory());
    }

    return "Unknown Skill";
    }

    /**
     * Check if a tag is too generic to be useful as a skill name
     */
    private boolean isGenericTag(String tag) {
    Set<String> genericTags = Set.of(
        "beginner", "intermediate", "advanced", 
        "course", "tutorial", "lesson", "training",
        "external", "internal", "online", "certification"
    );
    return genericTags.contains(tag);
    }

    /**
     * Format skill name for consistent display
     */
    private String formatSkillName(String skillName) {
    if (skillName == null || skillName.trim().isEmpty()) {
        return "Unknown Skill";
    }

    // Clean up the skill name
    String formatted = skillName.trim();

    // If it's a long course title, try to extract the main skill
    if (formatted.length() > 30) {
        // Look for patterns like "Learn JavaScript" -> "JavaScript"
        formatted = extractMainSkillFromTitle(formatted);
    }

    // Capitalize first letter of each word
    return Arrays.stream(formatted.split("\\s+"))
        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
        .collect(Collectors.joining(" "));
    }

    /**
     * Extract main skill from course title
     */
    private String extractMainSkillFromTitle(String title) {
    // Common patterns in course titles
    String[] patterns = {
        "Learn\\s+(.+?)\\s+(Programming|Development|Course|Tutorial|Fundamentals)",
        "(.+?)\\s+(Complete|Course|Tutorial|Guide|Bootcamp|Masterclass)",
        "Introduction\\s+to\\s+(.+)",
        "(.+?)\\s+for\\s+(Beginners|Developers|Everyone)",
        "Mastering\\s+(.+)",
        "(.+?)\\s+Fundamentals"
    };

    for (String pattern : patterns) {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(title);
        if (m.find()) {
            return m.group(1).trim();
        }
    }

    // If no pattern matches, return first 2-3 words
    String[] words = title.split("\\s+");
    if (words.length >= 2) {
        return String.join(" ", Arrays.copyOfRange(words, 0, Math.min(3, words.length)));
    }

    return title;
    }
    
    // Goal Completion Analysis
    public Map<String, Object> getGoalMetrics(User user) {
        Map<String, Object> metrics = new HashMap<>();
        
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        
        List<DailyGoal> weekGoals = dailyGoalRepository.findByUserIdAndGoalDateBetween(
            user.getId(), weekStart, weekEnd);
        
        List<DailyGoal> completedGoals = weekGoals.stream()
            .filter(goal -> goal.getIsCompleted())
            .collect(Collectors.toList());
        
        double completionRate = weekGoals.size() > 0 ? 
            (completedGoals.size() * 100.0 / weekGoals.size()) : 0.0;
        
        metrics.put("totalGoalsThisWeek", weekGoals.size());
        metrics.put("completedGoalsThisWeek", completedGoals.size());
        metrics.put("goalCompletionRate", Math.round(completionRate * 10.0) / 10.0);
        
        return metrics;
    }
    
    // Competitive Analysis
    public Map<String, Object> getCompetitiveMetrics(User user) {
        Map<String, Object> metrics = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        Optional<LeaderboardEntry> todayEntry = leaderboardEntryRepository.findByUserAndDate(user, today);
        
        if (todayEntry.isPresent()) {
            LeaderboardEntry entry = todayEntry.get();
            Integer rank = leaderboardEntryRepository.findUserDailyRank(user, today);
            
            metrics.put("currentRank", rank != null ? rank : 999);
            metrics.put("totalPoints", entry.getTotalPoints());
            metrics.put("dailyPoints", entry.getDailyPoints());
        } else {
            metrics.put("currentRank", 999);
            metrics.put("totalPoints", 0);
            metrics.put("dailyPoints", 0);
        }
        
        return metrics;
    }
    
    // Helper methods for analysis
    public List<String> identifyStrengths(Map<String, Object> basicMetrics, Map<String, Double> skillScores) {
        List<String> strengths = new ArrayList<>();
        
        Double completionRate = (Double) basicMetrics.get("completionRate");
        Double averageScore = (Double) basicMetrics.get("averageScore");
        Integer loginStreak = (Integer) basicMetrics.get("loginStreak");
        
        if (completionRate >= 80.0) {
            strengths.add("Excellent course completion rate (" + completionRate + "%)");
        }
        
        if (averageScore >= 85.0) {
            strengths.add("High average scores (" + averageScore + "%)");
        }
        
        if (loginStreak >= 7) {
            strengths.add("Consistent daily learning streak (" + loginStreak + " days)");
        }
        
        // Find top performing skills
        skillScores.entrySet().stream()
            .filter(entry -> entry.getValue() >= 80.0)
            .forEach(entry -> strengths.add("Strong in " + entry.getKey() + 
                " (" + entry.getValue() + "% average)"));
        
        if (strengths.isEmpty()) {
            strengths.add("Active engagement in learning journey");
        }
        
        return strengths;
    }
    
    public List<String> identifyWeaknesses(Map<String, Object> basicMetrics, Map<String, Double> skillScores) {
        List<String> weaknesses = new ArrayList<>();
        
        Double completionRate = (Double) basicMetrics.get("completionRate");
        Double averageScore = (Double) basicMetrics.get("averageScore");
        Integer loginStreak = (Integer) basicMetrics.get("loginStreak");
        
        if (completionRate < 50.0) {
            weaknesses.add("Low course completion rate (" + completionRate + "%)");
        }
        
        if (averageScore < 70.0) {
            weaknesses.add("Below average scores (" + averageScore + "%)");
        }
        
        if (loginStreak < 3) {
            weaknesses.add("Inconsistent learning schedule");
        }
        
        // Find underperforming skills
        skillScores.entrySet().stream()
            .filter(entry -> entry.getValue() < 70.0)
            .forEach(entry -> weaknesses.add("Needs improvement in " + entry.getKey() + 
                " (" + entry.getValue() + "% average)"));
        
        if (weaknesses.isEmpty()) {
            weaknesses.add("No major weaknesses identified - keep up the good work!");
        }
        
        return weaknesses;
    }
    
    public List<String> generateRecommendations(Map<String, Object> basicMetrics, 
                                              Map<String, Double> skillScores,
                                              Map<String, Object> goalMetrics) {
        List<String> recommendations = new ArrayList<>();
        
        Double completionRate = (Double) basicMetrics.get("completionRate");
        Integer hoursThisWeek = (Integer) basicMetrics.get("hoursThisWeek");
        Integer targetHours = (Integer) basicMetrics.get("targetHoursPerWeek");
        Double goalCompletionRate = (Double) goalMetrics.get("goalCompletionRate");
        
        // Time management recommendations
        if (hoursThisWeek < targetHours * 0.7) {
            recommendations.add("Increase weekly study time to meet your " + targetHours + 
                " hour target");
        }
        
        // Course completion recommendations
        if (completionRate < 70.0) {
            recommendations.add("Focus on completing enrolled courses before starting new ones");
        }
        
        // Goal setting recommendations
        if (goalCompletionRate < 60.0) {
            recommendations.add("Set more achievable daily goals to build momentum");
        }
        
        // Skill-specific recommendations
        String weakestSkill = skillScores.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
            
        if (weakestSkill != null) {
            recommendations.add("Dedicate extra time to improving " + weakestSkill + " skills");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Continue your excellent learning habits!");
        }
        
        return recommendations;
    }
}