package com.backend.TTP.service;

import com.backend.TTP.dto.ProfileRequest;
import com.backend.TTP.dto.ProfileResponse;
import com.backend.TTP.model.*;
import com.backend.TTP.repository.UserProfileRepository;
import com.backend.TTP.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class ProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final LearningPathService learningPathService;

    public ProfileService(UserProfileRepository userProfileRepository,
                         UserRepository userRepository,
                         LearningPathService learningPathService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.learningPathService = learningPathService;
    }

    public ProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        return mapToResponse(profile);
    }
    
    @Transactional
    public ProfileResponse createOrUpdateProfile(ProfileRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(new UserProfile());
        
        profile.setUser(user);
        profile.setCareerStage(request.getCareerStage());
        profile.getSkills().clear();
        if (request.getSkills() != null) {
            profile.getSkills().addAll(request.getSkills());
        }
        profile.setGoals(request.getGoals());
        profile.setHoursPerWeek(request.getHoursPerWeek());
        
        UserProfile savedProfile = userProfileRepository.save(profile);
        
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
        return mapToResponse(savedProfile);
    }

    private ProfileResponse mapToResponse(UserProfile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setCareerStage(profile.getCareerStage());
        response.setSkills(profile.getSkills());
        response.setGoals(profile.getGoals());
        response.setHoursPerWeek(profile.getHoursPerWeek());
        if(profile.getLearningPath() != null) {
            response.setLearningPath(profile.getLearningPath().getPathContent());
        }
        return response;
    }
}