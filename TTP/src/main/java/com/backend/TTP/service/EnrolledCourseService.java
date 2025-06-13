package com.backend.TTP.service;

import com.backend.TTP.dto.EnrolledCourseDTO;
import com.backend.TTP.dto.EnrollmentRequest;
import com.backend.TTP.dto.ExternalCourseEnrollmentDTO;
import com.backend.TTP.dto.ProgressUpdateRequest;
import com.backend.TTP.model.Course;
import com.backend.TTP.model.EnrolledCourse;
import com.backend.TTP.model.User;
import com.backend.TTP.model.UserProfile;
import com.backend.TTP.repository.CourseRepository;
import com.backend.TTP.repository.EnrolledCourseRepository;
import com.backend.TTP.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrolledCourseService {
    
    @Autowired
    private EnrolledCourseRepository enrolledCourseRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AchievementService achievementService;
    
    /**
     * Get all enrolled courses for a user
     */
    public List<EnrolledCourseDTO> getEnrolledCourses(User user) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUser(user);
        return enrolledCourses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Enroll in an existing course
     */
    public EnrolledCourseDTO enrollCourse(User user, EnrollmentRequest request) {
        Optional<Course> courseOpt = courseRepository.findById(request.getCourseId());
        
        if (!courseOpt.isPresent()) {
            throw new RuntimeException("Course not found");
        }
        
        Course course = courseOpt.get();
        
        // Check if already enrolled
        Optional<EnrolledCourse> existingEnrollment = enrolledCourseRepository.findByUserAndCourse(user, course);
        
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Already enrolled in this course");
        }
        
        // Create new enrollment
        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUser(user);
        enrolledCourse.setCourse(course);
        enrolledCourse.setEnrollmentDate(LocalDate.now());
        enrolledCourse.setTargetCompletionDate(request.getTargetCompletionDate());
        enrolledCourse.setProgressPercentage(0);
        enrolledCourse.setStatus("NOT_STARTED");
        enrolledCourse.setHoursSpentThisWeek(0);
        enrolledCourse.setWeekStartDate(getStartOfWeek(LocalDate.now()));
        
        enrolledCourse = enrolledCourseRepository.save(enrolledCourse);
        
        return convertToDTO(enrolledCourse);
    }
    
    /**
     * Enroll in an external course (Coursera, YouTube, etc.)
     */
    public EnrolledCourseDTO enrollExternalCourse(User user, ExternalCourseEnrollmentDTO request) {
        // Create a new course entity for the external course
        Course course = new Course();
        course.setTitle(request.getCourseName());
        course.setDescription("External course from " + request.getPlatform());
        course.setPlatform(request.getPlatform());
        course.setUrl(request.getCourseUrl());
        course.setEstimatedHours(request.getEstimatedHours());
        course.setSkillLevel("EXTERNAL");
        course.setCategory("EXTERNAL");
        course.setIsExternal(true);
        
        course = courseRepository.save(course);
        
        // Create enrollment
        EnrolledCourse enrolledCourse = new EnrolledCourse();
        enrolledCourse.setUser(user);
        enrolledCourse.setCourse(course);
        enrolledCourse.setEnrollmentDate(LocalDate.now());
        enrolledCourse.setTargetCompletionDate(request.getTargetCompletionDate());
        enrolledCourse.setProgressPercentage(0);
        enrolledCourse.setStatus("NOT_STARTED");
        enrolledCourse.setHoursSpentThisWeek(0);
        enrolledCourse.setWeekStartDate(getStartOfWeek(LocalDate.now()));
        
        enrolledCourse = enrolledCourseRepository.save(enrolledCourse);
        
        return convertToDTO(enrolledCourse);
    }
    
    public EnrolledCourseDTO updateProgress(User user, ProgressUpdateRequest request) {
        Optional<EnrolledCourse> enrolledCourseOpt = enrolledCourseRepository.findById(request.getEnrolledCourseId());
        
        if (!enrolledCourseOpt.isPresent()) {
            throw new RuntimeException("Enrolled course not found");
        }
        
        EnrolledCourse enrolledCourse = enrolledCourseOpt.get();
        
        // Verify the course belongs to the user
        if (!enrolledCourse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this course");
        }
        
        // Check if this is a course completion (was not completed before, now 100%)
        boolean wasCompleted = "COMPLETED".equals(enrolledCourse.getStatus());
        boolean isNowCompleted = request.getProgressPercentage() == 100;
        
        // Update progress
        enrolledCourse.setProgressPercentage(request.getProgressPercentage());
        
        // Add hours spent
        Integer currentHoursSpent = enrolledCourse.getHoursSpent() != null ? enrolledCourse.getHoursSpent() : 0;
        enrolledCourse.setHoursSpent(currentHoursSpent + request.getAdditionalHoursSpent());
        
        // Update weekly hours
        updateWeeklyHours(enrolledCourse, request.getAdditionalHoursSpent());
        
        // Update status based on progress
        if (request.getProgressPercentage() == 100) {
            enrolledCourse.setStatus("COMPLETED");
            enrolledCourse.setActualCompletionDate(LocalDate.now());
        } else if (request.getProgressPercentage() > 0) {
            enrolledCourse.setStatus("IN_PROGRESS");
        }
        
        enrolledCourse = enrolledCourseRepository.save(enrolledCourse);
        
        // NEW: Award achievement points for course completion
        if (!wasCompleted && isNowCompleted) {
            // Award base completion points (20 points)
            // Note: Course scoring with bonus points should be done separately via the course score endpoint
            try {
                achievementService.awardCourseCompletionPoints(user, enrolledCourse);
            } catch (Exception e) {
                // Log error but don't fail the course update
                System.err.println("Failed to award achievement points: " + e.getMessage());
            }
        }
        
        return convertToDTO(enrolledCourse);
    }
    
    /**
     * Get enrolled courses by status
     */
    public List<EnrolledCourseDTO> getEnrolledCoursesByStatus(User user, String status) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUserAndStatus(user, status);
        return enrolledCourses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert EnrolledCourse entity to DTO
     */
    private EnrolledCourseDTO convertToDTO(EnrolledCourse enrolledCourse) {
        EnrolledCourseDTO dto = new EnrolledCourseDTO();
        
        dto.setId(enrolledCourse.getId());
        dto.setCourseId(enrolledCourse.getCourse().getId());
        dto.setCourseTitle(enrolledCourse.getCourse().getTitle());
        dto.setPlatform(enrolledCourse.getCourse().getPlatform());
        dto.setUrl(enrolledCourse.getCourse().getUrl());
        dto.setEnrollmentDate(enrolledCourse.getEnrollmentDate());
        dto.setTargetCompletionDate(enrolledCourse.getTargetCompletionDate());
        dto.setActualCompletionDate(enrolledCourse.getActualCompletionDate());
        dto.setProgressPercentage(enrolledCourse.getProgressPercentage());
        dto.setStatus(enrolledCourse.getStatus());
        dto.setHoursSpent(enrolledCourse.getHoursSpent());
        dto.setEstimatedHours(enrolledCourse.getCourse().getEstimatedHours());
        dto.setCategory(enrolledCourse.getCourse().getCategory());
        
        return dto;
    }
    
    /**
     * Get statistics about enrolled courses
     */
    public Map<String, Object> getEnrollmentStatistics(User user) {
        List<EnrolledCourse> allCourses = enrolledCourseRepository.findByUser(user);
        
        int totalCourses = allCourses.size();
        int completedCourses = (int) allCourses.stream()
                .filter(c -> "COMPLETED".equals(c.getStatus()))
                .count();
        int inProgressCourses = (int) allCourses.stream()
                .filter(c -> "IN_PROGRESS".equals(c.getStatus()))
                .count();
        int notStartedCourses = (int) allCourses.stream()
                .filter(c -> "NOT_STARTED".equals(c.getStatus()))
                .count();
        
        int totalHoursSpent = allCourses.stream()
                .mapToInt(c -> c.getHoursSpent() != null ? c.getHoursSpent() : 0)
                .sum();
        
        // Calculate overall progress as average of all courses
        int overallProgress = totalCourses > 0 ? 
                (int) allCourses.stream()
                .mapToInt(c -> c.getProgressPercentage() != null ? c.getProgressPercentage() : 0)
                .average()
                .orElse(0) : 0;
        
        return Map.of(
            "totalCourses", totalCourses,
            "completedCourses", completedCourses,
            "inProgressCourses", inProgressCourses,
            "notStartedCourses", notStartedCourses,
            "totalHoursSpent", totalHoursSpent,
            "overallProgress", overallProgress
        );
    }
    
    /**
     * Check if the user has free time this week
     * Has available time if hours spent this week for all enrolled courses < hours per week set by user
     */
    public boolean hasAvailableTimeThisWeek(User user) {
        // Get user's hours per week setting
        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUser(user);
        if (!userProfileOpt.isPresent() || userProfileOpt.get().getHoursPerWeek() == null) {
            return true; 
        }
        
        Integer userHoursPerWeek = userProfileOpt.get().getHoursPerWeek();
        
        // Get all enrolled courses for the user
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUser(user);
        
        // Calculate total hours spent this week across all courses
        int totalHoursThisWeek = 0;
        LocalDate currentWeekStart = getStartOfWeek(LocalDate.now());
        
        for (EnrolledCourse course : enrolledCourses) {
            // Update weekly hours if needed (in case week has changed)
            updateWeeklyHours(course, 0);
            
            // Only count hours if the course's week matches current week
            if (course.getWeekStartDate() != null && course.getWeekStartDate().equals(currentWeekStart)) {
                totalHoursThisWeek += course.getHoursSpentThisWeek() != null ? course.getHoursSpentThisWeek() : 0;
            }
        }
        
        // User has available time if total hours this week is less than their weekly limit
        return totalHoursThisWeek < userHoursPerWeek;
    }
    
    /**
     * Get recommended supplementary videos for the user based on available time
     */
    public List<String> getSupplementaryVideos(User user, String category) {
        if (!hasAvailableTimeThisWeek(user)) {
            return new ArrayList<>();
        }
        
        // In a real implementation, this would come from the YouTube API
        // based on the user's learning path categories
        if (category.equals("DATA_SCIENCE")) {
            return List.of(
                "https://www.youtube.com/watch?v=ua-CiDNNj30", // FreeCodeCamp Data Science Course
                "https://www.youtube.com/watch?v=LHBE6Q9XlzI"  // FreeCodeCamp Python Data Science
            );
        } else if (category.equals("WEB_DEVELOPMENT")) {
            return List.of(
                "https://www.youtube.com/watch?v=PkZNo7MFNFg", // FreeCodeCamp JavaScript
                "https://www.youtube.com/watch?v=mU6anWqZJcc"  // FreeCodeCamp HTML/CSS
            );
        } else {
            return List.of(
                "https://www.youtube.com/watch?v=rfscVS0vtbw", // FreeCodeCamp Python
                "https://www.youtube.com/watch?v=zOjov-2OZ0E"  // FreeCodeCamp CS
            );
        }
    }
    
    /**
     * Helper method to get the start of the week (Monday)
     */
    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
    
    /**
     * Helper method to update weekly hours for a course
     */
    private void updateWeeklyHours(EnrolledCourse enrolledCourse, Integer additionalHours) {
        LocalDate currentWeekStart = getStartOfWeek(LocalDate.now());
        
        // If it's a new week, reset the weekly hours
        if (enrolledCourse.getWeekStartDate() == null || !enrolledCourse.getWeekStartDate().equals(currentWeekStart)) {
            enrolledCourse.setHoursSpentThisWeek(additionalHours != null ? additionalHours : 0);
            enrolledCourse.setWeekStartDate(currentWeekStart);
        } else {
            // Same week, add to existing hours
            Integer currentWeeklyHours = enrolledCourse.getHoursSpentThisWeek() != null ? enrolledCourse.getHoursSpentThisWeek() : 0;
            enrolledCourse.setHoursSpentThisWeek(currentWeeklyHours + (additionalHours != null ? additionalHours : 0));
        }
    }
    
    /**
     * Get total hours spent this week across all courses for a user
     */
    public int getTotalHoursThisWeek(User user) {
        List<EnrolledCourse> enrolledCourses = enrolledCourseRepository.findByUser(user);
        int totalHours = 0;
        LocalDate currentWeekStart = getStartOfWeek(LocalDate.now());
        
        for (EnrolledCourse course : enrolledCourses) {
            updateWeeklyHours(course, 0); // Update weekly tracking if needed
            
            if (course.getWeekStartDate() != null && course.getWeekStartDate().equals(currentWeekStart)) {
                totalHours += course.getHoursSpentThisWeek() != null ? course.getHoursSpentThisWeek() : 0;
            }
        }
        
        return totalHours;
    }
}