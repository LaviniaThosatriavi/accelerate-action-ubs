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
        // Find the enrolled course by ID directly (not by courseId)
        EnrolledCourse enrolledCourse = enrolledCourseRepository.findById(request.getEnrolledCourseId())
                .orElseThrow(() -> new RuntimeException("Enrolled course not found"));
        
        // Verify the enrolled course belongs to this user
        if (!enrolledCourse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This enrolled course does not belong to the current user");
        }
        
        // Check if course is completed
        if (!"COMPLETED".equals(enrolledCourse.getStatus())) {
            throw new RuntimeException("Course must be completed before scoring");
        }
        
        // Check if score already exists using enrolledCourse.getCourse().getId()
        Long courseId = enrolledCourse.getCourse().getId();
        Optional<CourseScore> existingScore = courseScoreRepository.findByUserAndCourseId(user, courseId);
        
        CourseScore courseScore;
        if (existingScore.isPresent()) {
            courseScore = existingScore.get();
        } else {
            courseScore = new CourseScore();
            courseScore.setUser(user);
            courseScore.setCourseId(courseId);
            courseScore.setCompletionDate(LocalDateTime.now());
        }
        
        // Set score data
        courseScore.setScore(request.getScore());
        courseScore.setMaxScore(request.getMaxScore());
        courseScore.setNotes(request.getNotes());
        
        // Calculate points based on percentage
        Double percentage = (request.getScore().doubleValue() / request.getMaxScore().doubleValue()) * 100;
        Integer basePoints = 20;
        Integer scoreBonus = calculateScoreBonus(percentage.intValue());
        Integer totalPoints = basePoints + scoreBonus;
        
        courseScore.setPointsEarned(totalPoints);
        courseScore = courseScoreRepository.save(courseScore);
        
        // Award points only for new scores
        if (!existingScore.isPresent()) {
            awardPoints(user, "COURSE_COMPLETION", totalPoints, 
                    "Completed course with score: " + percentage.intValue() + "%", 
                    enrolledCourse.getId());
        }
        
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
     * Get leaderboard - SIMPLIFIED FOR DAILY ONLY
     */
    public LeaderboardResponse getLeaderboard(User user, String period, Integer limit) {
        // Only support daily leaderboard now
        List<LeaderboardUserDTO> topUsers = new ArrayList<>();
        LeaderboardUserDTO currentUserEntry = null;
        
        LocalDate today = LocalDate.now();
        
        // Ensure leaderboard is up to date (but don't trigger from awardPoints to avoid loop)
        updateDailyLeaderboard();
        
        // Get daily leaderboard (showing total points, not daily points)
        topUsers = getDailyLeaderboard(today, limit);
        currentUserEntry = getCurrentUserLeaderboardPosition(user, today);
        
        // Set isCurrentUser flag for users in the top list
        for (LeaderboardUserDTO topUser : topUsers) {
            if (topUser.getUserId().equals(user.getId())) {
                topUser.setIsCurrentUser(true);
                if (currentUserEntry == null) {
                    currentUserEntry = topUser;
                } else {
                    currentUserEntry.setIsCurrentUser(true);
                }
                break;
            }
        }
        
        LeaderboardResponse response = new LeaderboardResponse();
        response.setTopUsers(topUsers);
        response.setCurrentUser(currentUserEntry);
        response.setPeriod("daily");
        
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
     * FIXED: Update leaderboard with TOTAL POINTS - NO BONUS AWARDING
     */
    @Transactional
    public void updateDailyLeaderboard() {
        LocalDate today = LocalDate.now();
        
        // Get all users with their profiles
        List<UserProfile> allProfiles = userProfileRepository.findAll();
        
        for (UserProfile profile : allProfiles) {
            User user = profile.getUser();
            
            // Use TOTAL POINTS from profile instead of calculating daily points
            Integer totalPoints = profile.getTotalPoints();
            
            // Create or update leaderboard entry
            LeaderboardEntry entry = leaderboardEntryRepository.findByUserAndDate(user, today)
                    .orElse(new LeaderboardEntry());
            
            entry.setUser(user);
            entry.setDate(today);
            entry.setDailyPoints(totalPoints); // Store total points in dailyPoints field
            entry.setWeeklyPoints(totalPoints); // Keep these for compatibility
            entry.setMonthlyPoints(totalPoints);
            entry.setTotalPoints(totalPoints);
            
            leaderboardEntryRepository.save(entry);
        }
        
        // REMOVED: Don't award leaderboard bonuses here to avoid infinite loop
        // awardLeaderboardBonuses(today);
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
     * FIXED: Award points WITHOUT triggering leaderboard update to avoid loop
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
            
            // Award badge level up bonus (but don't trigger leaderboard update here)
            awardBadgeLevelUpBonusWithoutUpdate(user, newBadgeLevel);
        }
        
        userProfileRepository.save(profile);
        
        // Update the user's leaderboard entry immediately (but only for this user)
        updateUserLeaderboardEntry(user);
    }
    
    /**
     * NEW: Update only one user's leaderboard entry to avoid full leaderboard recalculation
     */
    @Transactional
    public void updateUserLeaderboardEntry(User user) {
        LocalDate today = LocalDate.now();
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        
        if (profile != null) {
            Integer totalPoints = profile.getTotalPoints();
            
            LeaderboardEntry entry = leaderboardEntryRepository.findByUserAndDate(user, today)
                    .orElse(new LeaderboardEntry());
            
            entry.setUser(user);
            entry.setDate(today);
            entry.setDailyPoints(totalPoints);
            entry.setWeeklyPoints(totalPoints);
            entry.setMonthlyPoints(totalPoints);
            entry.setTotalPoints(totalPoints);
            
            leaderboardEntryRepository.save(entry);
        }
    }
    
    // Private helper methods
    
    /**
     * FIXED: Award badge bonus without triggering leaderboard update
     */
    private void awardBadgeLevelUpBonusWithoutUpdate(User user, String newBadgeLevel) {
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
    
    /**
     * FIXED: Daily leaderboard now shows TOTAL POINTS
     */
    private List<LeaderboardUserDTO> getDailyLeaderboard(LocalDate date, Integer limit) {
        List<LeaderboardEntry> entries = leaderboardEntryRepository.findDailyLeaderboard(date);
        
        List<LeaderboardUserDTO> result = new ArrayList<>();
        int currentRank = 1;
        Integer previousPoints = null;
        
        for (int i = 0; i < Math.min(entries.size(), limit); i++) {
            LeaderboardEntry entry = entries.get(i);
            
            // Handle ranking with ties
            if (previousPoints == null || !previousPoints.equals(entry.getTotalPoints())) {
                currentRank = i + 1;
            }
            
            LeaderboardUserDTO dto = new LeaderboardUserDTO();
            dto.setUserId(entry.getUser().getId());
            dto.setUsername(entry.getUser().getUsername());
            dto.setPoints(entry.getTotalPoints()); // Use total points instead of daily points
            dto.setRank(currentRank);
            dto.setIsCurrentUser(false); // Will be set later in main method
            
            UserProfile profile = userProfileRepository.findByUser(entry.getUser()).orElse(null);
            dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
            
            result.add(dto);
            previousPoints = entry.getTotalPoints();
        }
        
        return result;
    }
    
    /**
     * FIXED: Get current user position using total points
     */
    private LeaderboardUserDTO getCurrentUserLeaderboardPosition(User user, LocalDate date) {
        LeaderboardEntry entry = leaderboardEntryRepository.findByUserAndDate(user, date).orElse(null);
        if (entry == null) return null;
        
        LeaderboardUserDTO dto = new LeaderboardUserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setIsCurrentUser(true);
        dto.setPoints(entry.getTotalPoints()); // Use total points
        
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);
        dto.setBadgeLevel(profile != null ? profile.getCurrentBadgeLevel() : "NOVICE");
        
        // Calculate rank based on total points
        Integer rank = leaderboardEntryRepository.findUserDailyRank(user, date);
        dto.setRank(rank != null ? rank : 999);
        
        return dto;
    }
}