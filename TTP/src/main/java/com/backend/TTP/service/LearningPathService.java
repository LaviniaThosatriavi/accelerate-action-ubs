package com.backend.TTP.service;

import com.backend.TTP.model.LearningPath;
import com.backend.TTP.model.UserProfile;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class LearningPathService {

    public LearningPath generateLearningPath(UserProfile profile) {
        LearningPath path = new LearningPath();
        path.setCreatedDate(LocalDate.now());
        
        // Generate learning path content based on profile
        StringBuilder pathContent = new StringBuilder();
        pathContent.append("# Personalized Learning Path\n\n");
        
        // Career stage specific content
        pathContent.append("## Based on your career stage: ").append(profile.getCareerStage()).append("\n\n");
        
        // Skills specific content
        pathContent.append("## Building on your skills:\n");
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            for (String skill : profile.getSkills()) {
                pathContent.append("- ").append(skill).append("\n");
            }
        } else {
            pathContent.append("- No skills specified yet\n");
        }
        pathContent.append("\n");
        
        // Goals specific content
        pathContent.append("## To achieve your goals: ").append(profile.getGoals()).append("\n\n");
        
        // Time commitment
        pathContent.append("## Weekly commitment: ").append(profile.getHoursPerWeek()).append(" hours\n\n");
        
        // Sample learning path (this would be more sophisticated in a real implementation)
        pathContent.append("## Recommended Learning Path\n");
        pathContent.append("1. Foundations (Week 1-2)\n");
        pathContent.append("2. Core Skills (Week 3-5)\n");
        pathContent.append("3. Advanced Topics (Week 6-8)\n");
        pathContent.append("4. Project Work (Week 9-12)\n");
        
        path.setPathContent(pathContent.toString());
        return path;
    }
}