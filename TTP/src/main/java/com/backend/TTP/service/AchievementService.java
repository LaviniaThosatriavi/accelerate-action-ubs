package com.backend.TTP.service;

import com.backend.TTP.dto.*;
import com.backend.TTP.model.*;
import com.backend.TTP.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AchievementService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private CourseScoreRepository courseScoreRepository;
    
    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    
    @Autowired
    private LeaderboardEntryRepository leaderboardEntryRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    private static final Map<String, Integer> BADGE_THRESHOLDS = Map.of(
        "NOVICE", 0,
        "APPRENTICE", 100,
        "SCHOLAR", 300,
        "EXPERT", 700,
        "MASTER", 1500
    );
    
    /**
     * Get user's achievement profile
     */
    public AchievementProfileResponse getAchievementProfile(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        Integer totalPoints = profile.getTotalPoints();
        String currentBadge = profile.getCurrentBadgeLevel();
        String nextBadge = getNextBadgeLevel(currentBadge);
        Integer pointsToNext = getPointsToNextLevel(totalPoints, nextBadge);
        
        Long completedCourses = enrolledCourseRepository.countByUserAndStatus(user, "COMPLETED");
        Double averageScore = courseScoreRepository.findAverageScoreByUser(user);
        
        AchievementProfileResponse response = new AchievementProfileResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setTotalPoints(totalPoints);
        response.setCurrentBadgeLevel(currentBadge);
        response.setPointsToNextLevel(pointsToNext);
        response.setNextBadgeLevel(nextBadge);
        response.setLoginStreak(profile.getLoginStreak());
        response.setCompletedCourses(completedCourses.intValue());
        response.setAverageScore(averageScore);
        
        return response;
    }
    
    /**
     * Get all badge levels with requirements
     */
    public BadgeLevelResponse getBadgeLevels() {
        List<BadgeLevelResponse.BadgeLevel> badgeLevels = new ArrayList<>();
        
        badgeLevels.add(new BadgeLevelResponse.BadgeLevel("NOVICE", 0, 99, "Learning Explorer", "/badges/novice.png"));
        badgeLevels.add(new BadgeLevelResponse.BadgeLevel("APPRENTICE", 100, 299, "Knowledge Seeker", "/badges/apprentice.png"));
        badgeLevels.add(new BadgeLevelResponse.BadgeLevel("SCHOLAR", 300, 699, "Dedicated Learner", "/badges/scholar.png"));
        badgeLevels.add(new BadgeLevelResponse.BadgeLevel("EXPERT", 700, 1499, "Skill Master", "/badges/expert.png"));
        badgeLevels.add(new BadgeLevelResponse.BadgeLevel("MASTER", 1500, null, "Learning Champion", "/badges/master.png"));
        
        BadgeLevelResponse response = new BadgeLevelResponse();
        response.setBadgeLevels(badgeLevels);
        return response;
    }
    
    /**
     * Record course score and award points
     */
    @Transactional
    public CourseScore recordCourseScore(User user, CourseScoreRequest request) {
        EnrolledCourse enrolledCourse = enrolledCourseRepository.findById(request.getEnrolledCourseId())
                .orElseThrow(() -> new RuntimeException("Enrolled course not found"));
        
        // Verify course belongs to user
        if (!enrolledCourse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to score this course");
        }
        
        // Check if course is completed
        if (!"COMPLETED".equals(enrolledCourse.getStatus())) {
            throw new RuntimeException("Course must be completed before scoring");
        }
        
        // Check if score already exists
        Optional<CourseScore> existingScore = courseScoreRepository.findByUserAndEnrolledCourse(user, enrolledCourse);
        if (existingScore.isPresent()) {
            throw new RuntimeException("Score already recorded for this course");
        }
        
        // Create course score
        CourseScore courseScore = new CourseScore();
        courseScore.setUser(user);
        courseScore.setEnrolledCourse(enrolledCourse);
        courseScore.setScore(request.getScore());
        courseScore.setCompletionDate(request.getCompletionDate());
        
        // Calculate points
        Integer basePoints = 20; // Base points for completion
        Integer scoreBonus = calculateScoreBonus(request.getScore());
        Integer totalPoints = basePoints + scoreBonus;
        
        courseScore.setPointsEarned(totalPoints);
        courseScore = courseScoreRepository.save(courseScore);
        
        // Award points
        awardPoints(user, "COURSE_COMPLETION", totalPoints, 
                   "Completed course: " + enrolledCourse.getCourse().getTitle(), 
                   enrolledCourse.getId());
        
        return courseScore;
    }
    
    /**
     * Award daily login points
     */
    @Transactional
    public void recordDailyLogin(User user) {
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = profile.getLastLoginDate();
        
        if (lastLogin == null || !lastLogin.equals(today)) {
            // Calculate streak
            Integer currentStreak = 1;
            if (lastLogin != null && lastLogin.equals(today.minusDays(1))) {
                currentStreak = profile.getLoginStreak() + 1;
            } else if (lastLogin != null && lastLogin.isBefore(today.minusDays(1))) {
                currentStreak = 1; // Reset streak
            }
            
            profile.setLoginStreak(currentStreak);
            profile.setLastLoginDate(today);
            userProfileRepository.save(profile);
            
            // Award login points
            awardPoints(user, "DAILY_LOGIN", 1, "Daily login streak: " + currentStreak, null);
        }
    }
    
    /**
     * Get leaderboard
     */
    public LeaderboardResponse getLeaderboard(User user, String period, Integer limit) {
        List<LeaderboardUserDTO> topUsers = new ArrayList<>();
        LeaderboardUserDTO currentUserEntry = null;
        
        LocalDate today = LocalDate.now();
        
        switch (period.toLowerCase()) {
            case "daily":
                topUsers = getDailyLeaderboard(today, limit);
                currentUserEntry = getCurrentUserLeaderboardPosition(user, period, today);
                break;
            case "weekly":
                LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
                LocalDate weekEnd = weekStart.plusDays(6);
                topUsers = getWeeklyLeaderboard(weekStart, weekEnd, limit);
                currentUserEntry = getCurrentUserLeaderboardPosition(user, period, today);
                break;
            case "monthly":
                topUsers = getMonthlyLeaderboard(today.getYear(), today.getMonthValue(), limit);
                currentUserEntry = getCurrentUserLeaderboardPosition(user, period, today);
                break;
        }
        
        LeaderboardResponse response = new LeaderboardResponse();
        response.setTopUsers(topUsers);
        response.setCurrentUser(currentUserEntry);
        response.setPeriod(period);
        
        return response;
    }
    
    /**
     * Get point history for user
     */
    public PointHistoryResponse getPointHistory(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<PointHistory> history;
        
        if (startDate != null && endDate != null) {
            history = pointHistoryRepository.findByUserAndEarnedAtBetween(user, startDate, endDate);
        } else {
            history = pointHistoryRepository.findByUserOrderByEarnedAtDesc(user);
        }
        
        List<PointHistoryResponse.PointHistoryItem> items = history.stream()
                .map(ph -> new PointHistoryResponse.PointHistoryItem(
                        ph.getActivityType(),
                        ph.getPointsEarned(),
                        ph.getEarnedAt(),
                        ph.getDescription()
                ))
                .collect(Collectors.toList());
        
        Integer totalPoints = pointHistoryRepository.getTotalPointsByUser(user);
        
        PointHistoryResponse response = new PointHistoryResponse();
        response.setPointHistory(items);
        response.setTotalPoints(totalPoints != null ? totalPoints : 0);
        
        return response;
    }
    
    /**
     * Calculate and update leaderboard rankings daily
     */
    @Transactional
    public void updateDailyLeaderboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        
        // Get all users with their daily points
        List<UserProfile> allProfiles = userProfileRepository.findAll();
        
        for (UserProfile profile : allProfiles) {
            User user = profile.getUser();
            
            // Calculate daily points
            Integer dailyPoints = pointHistoryRepository.getPointsByUserSince(user, startOfDay);
            if (dailyPoints == null) dailyPoints = 0;
            
            // Calculate weekly points (from Monday)
            LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
            Integer weeklyPoints = pointHistoryRepository.getPointsByUserSince(user, weekStart.atStartOfDay());
            if (weeklyPoints == null) weeklyPoints = 0;
            
            // Calculate monthly points
            LocalDate monthStart = today.withDayOfMonth(1);
            Integer monthlyPoints = pointHistoryRepository.getPointsByUserSince(user, monthStart.atStartOfDay());
            if (monthlyPoints == null) monthlyPoints = 0;
            
            // Create or update leaderboard entry
            LeaderboardEntry entry = leaderboardEntryRepository.findByUserAndDate(user, today)
                    .orElse(new LeaderboardEntry());
            
            entry.setUser(user);
            entry.setDate(today);
            entry.setDailyPoints(dailyPoints);
            entry.setWeeklyPoints(weeklyPoints);
            entry.setMonthlyPoints(monthlyPoints);
            entry.setTotalPoints(profile.getTotalPoints());
            
            leaderboardEntryRepository.save(entry);
        }
        
        // Award leaderboard bonus points
        awardLeaderboardBonuses(today);
    }
    
    /**
     * Award points for course completion (called from EnrolledCourseService)
     */
    @Transactional
    public void awardCourseCompletionPoints(User user, EnrolledCourse enrolledCourse) {
        // Award base completion points
        awardPoints(user, "COURSE_COMPLETION", 20, 
                   "Completed course: " + enrolledCourse.getCourse().getTitle(), 
                   enrolledCourse.getId());
    }
    
    /**
     * Make awardPoints method public so other services can use it
     */
    @Transactional
    public void awardPoints(User user, String activityType, Integer points, String description, Long relatedEntityId) {
        // Create point history record
        PointHistory pointHistory = new PointHistory();
        pointHistory.setUser(user);
        pointHistory.setActivityType(activityType);
        pointHistory.setPointsEarned(points);
        pointHistory.setEarnedAt(LocalDateTime.now());
        pointHistory.setDescription(description);
        pointHistory.setRelatedEntityId(relatedEntityId);
        
        pointHistoryRepository.save(pointHistory);
        
        // Update user's total points
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        profile.setTotalPoints(profile.getTotalPoints() + points);
        
        // Check for badge level up
        String newBadgeLevel = calculateBadgeLevel(profile.getTotalPoints());
        if (!newBadgeLevel.equals(profile.getCurrentBadgeLevel())) {
            profile.setCurrentBadgeLevel(newBadgeLevel);
            
            // Award badge level up bonus
            awardBadgeLevelUpBonus(user, newBadgeLevel);
        }
        
        userProfileRepository.save(profile);
    }
    
    // Private helper methods
    
    private void awardBadgeLevelUpBonus(User user, String newBadgeLevel) {
        int bonusPoints = 0;
        switch (newBadgeLevel) {
            case "APPRENTICE": bonusPoints = 10; break;
            case "SCHOLAR": bonusPoints = 25; break;
            case "EXPERT": bonusPoints = 50; break;
            case "MASTER": bonusPoints = 100; break;
        }
        
        if (bonusPoints > 0) {
            // Create separate point history entry for badge bonus
            PointHistory pointHistory = new PointHistory();
            pointHistory.setUser(user);
            pointHistory.setActivityType("BADGE_LEVEL_UP");
            pointHistory.setPointsEarned(bonusPoints);
            pointHistory.setEarnedAt(LocalDateTime.now());
            pointHistory.setDescription("Reached " + newBadgeLevel + " level!");
            
            pointHistoryRepository.save(pointHistory);
            
            // Update total points again
            UserProfile profile = userProfileRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("User profile not found"));
            profile.setTotalPoints(profile.getTotalPoints() + bonusPoints);
            userProfileRepository.save(profile);
        }
    }
    
    private Integer calculateScoreBonus(Integer score) {
        if (score >= 90) return 5;
        if (score >= 80) return 3;
        if (score >= 70) return 1;
        return 0;
    }
    
    private String calculateBadgeLevel(Integer totalPoints) {
        if (totalPoints >= 1500) return "MASTER";
        if (totalPoints >= 700) return "EXPERT";
        if (totalPoints >= 300) return "SCHOLAR";
        if (totalPoints >= 100) return "APPRENTICE";
        return "NOVICE";
    }
    
    private String getNextBadgeLevel(String currentLevel) {
        switch (currentLevel) {
            case "NOVICE": return "APPRENTICE";
            case "APPRENTICE": return "SCHOLAR";
            case "SCHOLAR": return "EXPERT";
            case "EXPERT": return "MASTER";
            case "MASTER": return "MASTER";
            default: return "APPRENTICE";
        }
    }
    
    private Integer getPointsToNextLevel(Integer currentPoints, String nextLevel) {
        if ("MASTER".equals(nextLevel) && currentPoints >= 1500) {
            return 0; // Already at max level
        }
        Integer threshold = BADGE_THRESHOLDS.get(nextLevel);
        return threshold != null ? Math.max(0, threshold - currentPoints) : 0;
    }
    
    private List<LeaderboardUserDTO> getDailyLeaderboard(LocalDate date, Integer limit) {
        List<LeaderboardEntry> entries = leaderboardEntryRepository.findDailyLeaderboard(date);
        
        List<LeaderboardUserDTO> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousPoints = null;
        
        for (int i = 0; i < Math.min(entries.size(), limit); i++) {
            LeaderboardEntry entry = entries.get(i);
            
            // If points are different from previous, update rank to current position
            if (previousPoints == null || !previousPoints.equals(entry.getDailyPoints())) {
                currentRank = i + 1;
            }
            // If points are the same, keep the same rank
            
            LeaderboardUserDTO dto = new LeaderboardUserDTO();
            dto.setUserId(entry.getUser().getId());
            dto.setUsername(entry.getUser().getUsername());
            dto.setPoints(entry.getDailyPoints());
            dto.setRank(currentRank);
            
            UserProfile profile = userProfileRepository.findByUser(entry.getUser()).orElse(null);
            dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
            
            result.add(dto);
            previousPoints = entry.getDailyPoints();
        }
        
        return result;
    }
    
    private List<LeaderboardUserDTO> getWeeklyLeaderboard(LocalDate startDate, LocalDate endDate, Integer limit) {
        List<LeaderboardEntry> entries = leaderboardEntryRepository.findWeeklyLeaderboard(startDate, endDate);
        
        List<LeaderboardUserDTO> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousPoints = null;
        
        for (int i = 0; i < Math.min(entries.size(), limit); i++) {
            LeaderboardEntry entry = entries.get(i);
            
            // If points are different from previous, update rank to current position
            if (previousPoints == null || !previousPoints.equals(entry.getWeeklyPoints())) {
                currentRank = i + 1;
            }
            
            LeaderboardUserDTO dto = new LeaderboardUserDTO();
            dto.setUserId(entry.getUser().getId());
            dto.setUsername(entry.getUser().getUsername());
            dto.setPoints(entry.getWeeklyPoints());
            dto.setRank(currentRank);
            
            UserProfile profile = userProfileRepository.findByUser(entry.getUser()).orElse(null);
            dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
            
            result.add(dto);
            previousPoints = entry.getWeeklyPoints();
        }
        
        return result;
    }
    
    private List<LeaderboardUserDTO> getMonthlyLeaderboard(int year, int month, Integer limit) {
        List<LeaderboardEntry> entries = leaderboardEntryRepository.findMonthlyLeaderboard(year, month);
        
        List<LeaderboardUserDTO> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousPoints = null;
        
        for (int i = 0; i < Math.min(entries.size(), limit); i++) {
            LeaderboardEntry entry = entries.get(i);
            
            // If points are different from previous, update rank to current position
            if (previousPoints == null || !previousPoints.equals(entry.getMonthlyPoints())) {
                currentRank = i + 1;
            }
            
            LeaderboardUserDTO dto = new LeaderboardUserDTO();
            dto.setUserId(entry.getUser().getId());
            dto.setUsername(entry.getUser().getUsername());
            dto.setPoints(entry.getMonthlyPoints());
            dto.setRank(currentRank);
            
            UserProfile profile = userProfileRepository.findByUser(entry.getUser()).orElse(null);
            dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
            
            result.add(dto);
            previousPoints = entry.getMonthlyPoints();
        }
        
        return result;
    }
    
    private LeaderboardUserDTO getCurrentUserLeaderboardPosition(User user, String period, LocalDate date) {
        LeaderboardEntry entry = leaderboardEntryRepository.findByUserAndDate(user, date).orElse(null);
        if (entry == null) return null;
        
        LeaderboardUserDTO dto = new LeaderboardUserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setIsCurrentUser(true);
        
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
        
        switch (period.toLowerCase()) {
            case "daily":
                dto.setPoints(entry.getDailyPoints());
                dto.setRank(leaderboardEntryRepository.findUserDailyRank(user, date));
                break;
            case "weekly":
                dto.setPoints(entry.getWeeklyPoints());
                // Calculate weekly rank
                break;
            case "monthly":
                dto.setPoints(entry.getMonthlyPoints());
                // Calculate monthly rank
                break;
        }
        
        return dto;
    }
    
    private void awardLeaderboardBonuses(LocalDate date) {
        List<LeaderboardEntry> dailyTop = leaderboardEntryRepository.findDailyLeaderboard(date);
        
        // Award bonus points for top performers
        for (int i = 0; i < Math.min(10, dailyTop.size()); i++) {
            LeaderboardEntry entry = dailyTop.get(i);
            int bonusPoints = 0;
            
            if (i < 3) {
                bonusPoints = 20; // Top 3
            } else {
                bonusPoints = 10; // Top 10
            }
            
            if (bonusPoints > 0) {
                awardPoints(entry.getUser(), "LEADERBOARD_BONUS", bonusPoints,
                        "Daily leaderboard rank " + (i + 1), null);
            }
        }
    }
}