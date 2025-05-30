package com.backend.TTP.service;

import com.backend.TTP.model.LearningPath;
import com.backend.TTP.model.UserProfile;
import com.backend.TTP.service.SkillMappingService.LearningPathData;
import com.backend.TTP.service.SkillMappingService.WeekPlan;
import com.backend.TTP.service.SkillMappingService.VideoResource;
import com.backend.TTP.service.SkillMappingService.ProjectIdea;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class LearningPathService {
    
    private final SkillMappingService skillMappingService;
    
    public LearningPathService(SkillMappingService skillMappingService) {
        this.skillMappingService = skillMappingService;
    }

    public LearningPath generateLearningPath(UserProfile profile) {
        LearningPath path = new LearningPath();
        path.setCreatedDate(LocalDate.now());
        
        // Use SkillMappingService to generate a detailed learning path
        LearningPathData pathData = skillMappingService.generateLearningPath(profile);
        
        // Convert the detailed structure to a well-formatted string
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
        
        // Add the detailed weekly plan with resources and projects
        pathContent.append("## Detailed Weekly Learning Plan\n\n");
        
        if (pathData.getWeeks() != null && !pathData.getWeeks().isEmpty()) {
            for (WeekPlan week : pathData.getWeeks()) {
                pathContent.append("### Week ").append(week.getWeekNumber()).append(": ");
                
                if (week.isReviewWeek()) {
                    pathContent.append("Review Week\n\n");
                    pathContent.append(week.getDescription()).append("\n\n");
                    
                    if (week.getReviewSkills() != null && !week.getReviewSkills().isEmpty()) {
                        pathContent.append("**Review Skills:**\n");
                        for (String skill : week.getReviewSkills()) {
                            pathContent.append("- ").append(skill).append("\n");
                        }
                        pathContent.append("\n");
                    }
                } else {
                    pathContent.append(week.getSkill()).append("\n\n");
                    pathContent.append(week.getDescription()).append("\n\n");
                    pathContent.append("**Hours allocated:** ").append(week.getHoursAllocated()).append("\n\n");
                }
                
                // Add learning resources with YouTube links
                if (week.getResources() != null && !week.getResources().isEmpty()) {
                    pathContent.append("**Learning Resources:**\n");
                    for (VideoResource resource : week.getResources()) {
                        pathContent.append("- [").append(resource.getTitle()).append("](")
                                 .append(resource.getUrl()).append("): ")
                                 .append(resource.getDescription()).append("\n");
                    }
                    pathContent.append("\n");
                }
                
                // Add project ideas
                if (week.getProjects() != null && !week.getProjects().isEmpty()) {
                    pathContent.append("**Project Ideas:**\n");
                    for (ProjectIdea project : week.getProjects()) {
                        pathContent.append("- **").append(project.getTitle()).append("**: ")
                                 .append(project.getDescription()).append("\n");
                    }
                    pathContent.append("\n");
                }
                
                pathContent.append("---\n\n");
            }
        } else {
            // Fallback to the simple plan if the detailed one wasn't generated
            pathContent.append("1. Foundations (Week 1-2)\n");
            pathContent.append("2. Core Skills (Week 3-5)\n");
            pathContent.append("3. Advanced Topics (Week 6-8)\n");
            pathContent.append("4. Project Work (Week 9-12)\n");
        }
        
        path.setPathContent(pathContent.toString());
        return path;
    }
}