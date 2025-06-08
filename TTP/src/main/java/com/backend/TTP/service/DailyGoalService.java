package com.backend.TTP.service;

import com.backend.TTP.dto.CreateDailyGoalRequest;
import com.backend.TTP.dto.DailyGoalDTO;
import com.backend.TTP.model.DailyGoal;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import com.backend.TTP.model.UserProfile;
import com.backend.TTP.repository.DailyGoalRepository;
import com.backend.TTP.repository.EnrolledCourseRepository;
import com.backend.TTP.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyGoalService {

    @Autowired
    private DailyGoalRepository dailyGoalRepository;
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired 
    private LearningPathService learningPathService;
    
    @Autowired
    private SkillMappingService skillMappingService;
    
    public List<DailyGoalDTO> getTodaysGoals(User user) {
        LocalDate today = LocalDate.now();
        List<DailyGoal> goals = dailyGoalRepository.findByUserIdAndGoalDate(user.getId(), today);
        
        // If no goals for today, generate recommendations
        if (goals.isEmpty()) {
            goals = generateRecommendedGoals(user, today);
        }
        
        return goals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DailyGoalDTO createUserGoal(User user, CreateDailyGoalRequest request) {
        // Get user profile to access hoursPerWeek
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
                
        // Check if adding this goal would exceed weekly hour limit
        LocalDate today = request.getGoalDate() != null ? request.getGoalDate() : LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        Double currentWeeklyHours = dailyGoalRepository.sumAllocatedHoursByUserIdAndDateRange(
                user.getId(), startOfWeek, endOfWeek);
        currentWeeklyHours = currentWeeklyHours != null ? currentWeeklyHours : 0.0;
        
        if (currentWeeklyHours + request.getAllocatedHours() > userProfile.getHoursPerWeek()) {
            throw new RuntimeException("Adding this goal would exceed your weekly hour limit of " + 
                    userProfile.getHoursPerWeek() + " hours. Currently used: " + currentWeeklyHours + " hours.");
        }
        
        DailyGoal goal = new DailyGoal();
        goal.setUser(user);
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setAllocatedHours(request.getAllocatedHours());
        goal.setGoalDate(today);
        goal.setIsRecommended(false);
        goal.setIsCompleted(false);
        
        // If linked to a course, set the relationship
        if (request.getEnrolledCourseId() != null) {
            enrolledCourseRepository.findById(request.getEnrolledCourseId())
                    .ifPresent(goal::setEnrolledCourse);
        }
        
        goal.setResourceUrl(request.getResourceUrl());
        goal.setResourceType(request.getResourceType());
        
        DailyGoal saved = dailyGoalRepository.save(goal);
        return convertToDTO(saved);
    }
    
    public List<DailyGoalDTO> completeGoals(User user, List<Long> goalIds) {
        List<DailyGoal> goals = dailyGoalRepository.findAllById(goalIds);
        
        // Validate all goals belong to the user
        for (DailyGoal goal : goals) {
            if (!goal.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Goal with ID " + goal.getId() + " does not belong to the current user");
            }
        }
        
        // Mark goals as completed
        LocalDateTime now = LocalDateTime.now();
        for (DailyGoal goal : goals) {
            goal.setIsCompleted(true);
            goal.setCompletedAt(now);
            
            // Update enrolled course progress if applicable
            if (goal.getEnrolledCourse() != null) {
                updateCourseProgress(goal.getEnrolledCourse(), goal.getAllocatedHours());
            }
        }
        
        List<DailyGoal> updatedGoals = dailyGoalRepository.saveAll(goals);
        return updatedGoals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void updateCourseProgress(EnrolledCourse course, Double hoursSpent) {
        // Round to nearest integer for hours spent
        int additionalHours = (int) Math.round(hoursSpent);
        
        // Update hours spent
        course.setHoursSpent(course.getHoursSpent() + additionalHours);
        
        // Update status if not already in progress
        if ("NOT_STARTED".equals(course.getStatus())) {
            course.setStatus("IN_PROGRESS");
        }
        
        // Update progress percentage based on estimated hours
        if (course.getCourse() != null && course.getCourse().getEstimatedHours() != null && 
                course.getCourse().getEstimatedHours() > 0) {
            int estimatedHours = course.getCourse().getEstimatedHours();
            int newProgress = Math.min(100, (int) Math.round((double) course.getHoursSpent() / estimatedHours * 100));
            course.setProgressPercentage(newProgress);
            
            // If progress is 100%, mark as completed
            if (newProgress >= 100 && !"COMPLETED".equals(course.getStatus())) {
                course.setStatus("COMPLETED");
                course.setActualCompletionDate(LocalDate.now());
            }
        }
        
        enrolledCourseRepository.save(course);
    }
    
    private List<DailyGoal> generateRecommendedGoals(User user, LocalDate date) {
        // Get the user profile for settings
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        // Get the user's in-progress courses
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserAndStatus(
                user, "IN_PROGRESS");
        
        // Calculate remaining weekly hours
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        Double usedHoursThisWeek = dailyGoalRepository.sumAllocatedHoursByUserIdAndDateRange(
                user.getId(), startOfWeek, endOfWeek);
        usedHoursThisWeek = usedHoursThisWeek != null ? usedHoursThisWeek : 0.0;
        
        Double remainingHoursThisWeek = userProfile.getHoursPerWeek() - usedHoursThisWeek;
        
        // If no hours remaining, return empty list
        if (remainingHoursThisWeek <= 0) {
            return Collections.emptyList();
        }
        
        // Calculate days left in the week
        int daysLeftInWeek = 7 - date.getDayOfWeek().getValue() + 1; // Including today
        
        // Calculate average hours per day for the rest of the week
        Double hoursPerDay = remainingHoursThisWeek / daysLeftInWeek;
        
        // If hours per day is too small, allocate more for today
        Double todayTargetHours = Math.min(remainingHoursThisWeek, 
                Math.max(hoursPerDay, Math.min(4.0, remainingHoursThisWeek)));
        
        List<DailyGoal> recommendedGoals = new ArrayList<>();
        double allocatedHours = 0.0;
        
        // Create a priority queue for courses based on closeness to deadline
        PriorityQueue<EnrolledCourse> priorityCourses = new PriorityQueue<>(
                Comparator.comparing(course -> {
                    if (course.getTargetCompletionDate() == null) {
                        return LocalDate.now().plusYears(1); // Far future for no deadline
                    }
                    return course.getTargetCompletionDate();
                })
        );
        priorityCourses.addAll(enrolledCourses);
        
        // Allocate time to high-priority courses first
        while (!priorityCourses.isEmpty() && allocatedHours < todayTargetHours) {
            EnrolledCourse course = priorityCourses.poll();
            
            // Calculate how much time to allocate to this course
            Integer estimatedHours = course.getCourse().getEstimatedHours();
            if (estimatedHours == null || estimatedHours <= 0) {
                estimatedHours = 20; // Default if not specified
            }
            
            double remainingCourseHours = estimatedHours * (1 - course.getProgressPercentage() / 100.0);
            double courseHoursToday = Math.min(remainingCourseHours, 
                    Math.min(2.0, todayTargetHours - allocatedHours));
            
            if (courseHoursToday > 0.25) { // Only create goals for significant time allocations
                DailyGoal goal = new DailyGoal();
                goal.setUser(user);
                goal.setTitle("Continue " + course.getCourse().getTitle());
                goal.setDescription("Make progress on your enrolled course");
                goal.setAllocatedHours(courseHoursToday);
                goal.setGoalDate(date);
                goal.setIsRecommended(true);
                goal.setIsCompleted(false);
                goal.setEnrolledCourse(course);
                goal.setResourceUrl(course.getCourse().getUrl());
                goal.setResourceType("COURSE");
                
                recommendedGoals.add(goal);
                allocatedHours += courseHoursToday;
            }
        }

    // If we still have time, suggest skill-based videos or articles
    if (allocatedHours < todayTargetHours) {
        List<String> userSkills = userProfile.getSkills();
        
        // Select up to 3 skills to recommend content for
        List<String> prioritySkills = userSkills.isEmpty() ? 
                List.of("programming", "software development") : // Fallback if no skills
                userSkills.subList(0, Math.min(3, userSkills.size()));
        
        // Create learning context based on career stage
        String context = userProfile.getCareerStage() != null ? 
                userProfile.getCareerStage() : "beginner";
                
        double remainingToAllocate = todayTargetHours - allocatedHours;
        
        for (String skill : prioritySkills) {
            if (allocatedHours >= todayTargetHours) break;
            
            try {
                // Get video recommendations for this skill using the core YouTube search method
                List<SkillMappingService.VideoResource> videos = skillMappingService.searchYouTubeVideos(skill, context, 2);
                
                if (videos != null && !videos.isEmpty()) {
                    // Pick a video resource that hasn't been recommended recently
                    // Ideally would check against recent goals, but simplified here
                    SkillMappingService.VideoResource resource = videos.get(new Random().nextInt(videos.size()));
                    
                    // Default time allocation for a video (30 mins)
                    double resourceHours = 0.5;
                    
                    // Ensure we don't exceed daily allocation
                    resourceHours = Math.min(resourceHours, remainingToAllocate);
                    
                    if (resourceHours >= 0.25) { // Only recommend if it's worth at least 15 minutes
                        DailyGoal goal = new DailyGoal();
                        goal.setUser(user);
                        goal.setTitle("Learn " + skill + ": " + resource.getTitle());
                        goal.setDescription("Watch this recommended video about " + skill);
                        goal.setAllocatedHours(resourceHours);
                        goal.setGoalDate(date);
                        goal.setIsRecommended(true);
                        goal.setIsCompleted(false);
                        goal.setResourceUrl(resource.getUrl());
                        goal.setResourceType("VIDEO");
                        
                        recommendedGoals.add(goal);
                        allocatedHours += resourceHours;
                        remainingToAllocate -= resourceHours;
                    }
                }
            } catch (Exception e) {
                // If video search fails, try recommending a reading resource instead
                String articleUrl = generateArticleRecommendation(skill, context);
                if (articleUrl != null) {
                    double articleHours = 1.0; // Default time for reading an article
                    articleHours = Math.min(articleHours, remainingToAllocate);
                    
                    DailyGoal goal = new DailyGoal();
                    goal.setUser(user);
                    goal.setTitle("Study " + skill);
                    goal.setDescription("Read this article about " + skill);
                    goal.setAllocatedHours(articleHours);
                    goal.setGoalDate(date);
                    goal.setIsRecommended(true);
                    goal.setIsCompleted(false);
                    goal.setResourceUrl(articleUrl);
                    goal.setResourceType("ARTICLE");
                    
                    recommendedGoals.add(goal);
                    allocatedHours += articleHours;
                    remainingToAllocate -= articleHours;
                }
            }
        }
        
        // If we still have time and prioritized personal projects/practice
        if (allocatedHours < todayTargetHours && userProfile.getGoals() != null && 
                userProfile.getGoals().toLowerCase().contains("project")) {
            
            double practiceHours = Math.min(2.0, todayTargetHours - allocatedHours);
            
            if (practiceHours >= 0.5) {
                DailyGoal goal = new DailyGoal();
                goal.setUser(user);
                goal.setTitle("Personal project work");
                goal.setDescription("Spend time on your personal projects to apply what you're learning");
                goal.setAllocatedHours(practiceHours);
                goal.setGoalDate(date);
                goal.setIsRecommended(true);
                goal.setIsCompleted(false);
                goal.setResourceType("PRACTICE");
                
                recommendedGoals.add(goal);
                allocatedHours += practiceHours;
            }
        }
    }
        // Save all the recommended goals
        return dailyGoalRepository.saveAll(recommendedGoals);
    }

    private String generateArticleRecommendation(String skill, String context) {
        // Generate fallback article recommendations when video search fails
        Map<String, List<String>> articlesBySkill = new HashMap<>();
        
        // Predefined article resources for common skills
        articlesBySkill.put("java", List.of(
            "https://www.baeldung.com/java-tutorials",
            "https://dev.java/learn/",
            "https://docs.oracle.com/javase/tutorial/"
        ));
        
        articlesBySkill.put("spring", List.of(
            "https://spring.io/guides",
            "https://www.baeldung.com/spring-tutorial",
            "https://docs.spring.io/spring-framework/docs/current/reference/html/"
        ));
        
        articlesBySkill.put("react", List.of(
            "https://react.dev/learn",
            "https://www.freecodecamp.org/news/react-fundamentals-for-beginners/",
            "https://beta.reactjs.org/learn"
        ));
        
        // Fallback for any skill
        List<String> generalArticles = List.of(
            "https://medium.com/topic/" + skill.toLowerCase().replaceAll("\\s+", "-"),
            "https://dev.to/t/" + skill.toLowerCase().replaceAll("\\s+", ""),
            "https://www.freecodecamp.org/news/search/?query=" + skill.toLowerCase().replaceAll("\\s+", "%20")
        );
        
        // Find the best match for the skill
        String normalizedSkill = skill.toLowerCase();
        for (Map.Entry<String, List<String>> entry : articlesBySkill.entrySet()) {
            if (normalizedSkill.contains(entry.getKey()) || entry.getKey().contains(normalizedSkill)) {
                List<String> articles = entry.getValue();
                return articles.get(new Random().nextInt(articles.size()));
            }
        }
        
        // If no specific match, return a general article
        return generalArticles.get(new Random().nextInt(generalArticles.size()));
    }
    
    private DailyGoalDTO convertToDTO(DailyGoal goal) {
        DailyGoalDTO dto = new DailyGoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setAllocatedHours(goal.getAllocatedHours());
        dto.setGoalDate(goal.getGoalDate());
        dto.setIsCompleted(goal.getIsCompleted());
        dto.setIsRecommended(goal.getIsRecommended());
        dto.setCompletedAt(goal.getCompletedAt());
        dto.setResourceUrl(goal.getResourceUrl());
        dto.setResourceType(goal.getResourceType());
        
        if (goal.getEnrolledCourse() != null) {
            dto.setEnrolledCourseId(goal.getEnrolledCourse().getId());
            dto.setCourseName(goal.getEnrolledCourse().getCourse().getTitle());
        }
        
        return dto;
    }


    public List<DailyGoalDTO> getGoalsForDateRange(User user, LocalDate startDate, LocalDate endDate) {
        List<DailyGoal> goals = dailyGoalRepository.findByUserIdAndGoalDateBetween(
                user.getId(), startDate, endDate);
                
        return goals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DailyGoalDTO getGoalById(User user, Long goalId) {
        DailyGoal goal = dailyGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
                
        // Verify the goal belongs to the current user
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Goal with ID " + goalId + 
                    " does not belong to the current user");
        }
        
        return convertToDTO(goal);
    }

    public void deleteGoal(User user, Long goalId) {
        DailyGoal goal = dailyGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + goalId));
                
        // Verify the goal belongs to the current user
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Goal with ID " + goalId + 
                    " does not belong to the current user");
        }
        
        dailyGoalRepository.delete(goal);
    }
}