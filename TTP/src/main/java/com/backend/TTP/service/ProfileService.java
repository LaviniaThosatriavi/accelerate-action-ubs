package com.backend.TTP.service;

import com.backend.TTP.dto.ProfileRequest;
import com.backend.TTP.dto.ProfileResponse;
import com.backend.TTP.model.*;
import com.backend.TTP.repository.UserProfileRepository;
import com.backend.TTP.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProfileService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final LearningPathService learningPathService;
    @Autowired
    private AchievementService achievementService;

    public ProfileService(UserProfileRepository userProfileRepository,
                         UserRepository userRepository,
                         LearningPathService learningPathService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.learningPathService = learningPathService;
    }

    public ProfileResponse getProfile(String username) {
        logger.info("Getting profile for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        try {
            achievementService.recordDailyLogin(user);
        } catch (Exception e) {
            logger.warn("Failed to record daily login: {}", e.getMessage());
        }
        
        return mapToResponse(profile);
    }
    
    @Transactional
    public ProfileResponse createOrUpdateProfile(ProfileRequest request, String username) {
        try {
            logger.info("Creating or updating profile for user: {}", username);
            logger.debug("Profile request: {}", request);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserProfile profile = userProfileRepository.findByUser(user)
                    .orElse(new UserProfile());
            
            profile.setUser(user);
            
            // Handle null values with defaults
            profile.setCareerStage(request.getCareerStage() != null ? request.getCareerStage() : "beginner");
            
            // Handle skills list
            if (profile.getSkills() == null) {
                profile.setSkills(new ArrayList<>());
            } else {
                profile.getSkills().clear();
            }
            
            if (request.getSkills() != null) {
                profile.getSkills().addAll(request.getSkills());
            }
            
            profile.setGoals(request.getGoals() != null ? request.getGoals() : "improve skills");
            profile.setHoursPerWeek(request.getHoursPerWeek() != null ? request.getHoursPerWeek() : 5);
            
            UserProfile savedProfile = userProfileRepository.save(profile);
            logger.info("Saved user profile with ID: {}", savedProfile.getId());
            
            try {
                // Generate learning path after saving the profile
                LearningPath learningPath = learningPathService.generateLearningPath(savedProfile);
                
                if (savedProfile.getLearningPath() == null) {
                    learningPath.setUserProfile(savedProfile);
                    savedProfile.setLearningPath(learningPath);
                } else {
                    savedProfile.getLearningPath().setPathContent(learningPath.getPathContent());
                    savedProfile.getLearningPath().setCreatedDate(learningPath.getCreatedDate());
                }
                
                savedProfile = userProfileRepository.save(savedProfile);
                logger.info("Updated profile with learning path for user: {}", username);
                
            } catch (Exception e) {
                logger.error("Error generating learning path: {}", e.getMessage(), e);
                logger.info("Continuing with profile creation despite learning path generation error");
                // Continue even if learning path generation fails
            }
            
            return mapToResponse(savedProfile);
            
        } catch (Exception e) {
            logger.error("Error in createOrUpdateProfile: {}", e.getMessage(), e);
            throw e;
        }
    }

    private ProfileResponse mapToResponse(UserProfile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setCareerStage(profile.getCareerStage());
        response.setSkills(profile.getSkills());
        response.setGoals(profile.getGoals());
        response.setHoursPerWeek(profile.getHoursPerWeek());
        
        if (profile.getLearningPath() != null) {
            response.setLearningPath(profile.getLearningPath().getPathContent());
        }
        
        // SAFE handling of achievement fields with null checks
        response.setTotalPoints(profile.getTotalPoints() != null ? profile.getTotalPoints() : 0);
        response.setCurrentBadgeLevel(profile.getCurrentBadgeLevel() != null ? profile.getCurrentBadgeLevel() : "NOVICE");
        response.setLoginStreak(profile.getLoginStreak() != null ? profile.getLoginStreak() : 0);
        
        return response;
    }
}