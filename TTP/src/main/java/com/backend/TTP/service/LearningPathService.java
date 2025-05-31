package com.backend.TTP.service;

import com.backend.TTP.model.LearningPath;
import com.backend.TTP.model.UserProfile;
import com.backend.TTP.service.SkillMappingService.LearningPathData;
import com.backend.TTP.service.SkillMappingService.WeekPlan;
import com.backend.TTP.service.SkillMappingService.VideoResource;
import com.backend.TTP.service.SkillMappingService.ProjectIdea;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.Collections;

@Service
public class LearningPathService {
    private static final Logger logger = LoggerFactory.getLogger(LearningPathService.class);

    private final SkillMappingService skillMappingService;

    public LearningPathService(SkillMappingService skillMappingService) {
        this.skillMappingService = skillMappingService;
    }

    public LearningPath generateLearningPath(UserProfile profile) {
        try {
            logger.info("Generating learning path for user: {}", profile.getUser().getUsername());
            LearningPath path = new LearningPath();
            path.setCreatedDate(LocalDate.now());
            
            // Validate profile data
            if (profile.getSkills() == null) {
                logger.warn("Skills list is null, setting to empty list");
                profile.setSkills(Collections.emptyList());
            }
            
            if (profile.getCareerStage() == null) {
                logger.warn("Career stage is null, setting to 'beginner'");
                profile.setCareerStage("beginner");
            }
            
            if (profile.getGoals() == null) {
                logger.warn("Goals is null, setting to 'improve skills'");
                profile.setGoals("improve skills");
            }
            
            if (profile.getHoursPerWeek() == null) {
                logger.warn("Hours per week is null, setting to 5");
                profile.setHoursPerWeek(5);
            }
            
            logger.debug("Calling skillMappingService with profile: career={}, skills={}, goals={}, hours={}",
                profile.getCareerStage(), profile.getSkills(), profile.getGoals(), profile.getHoursPerWeek());
            
            LearningPathData pathData = skillMappingService.generateLearningPath(profile);
            logger.debug("Received learning path data from skillMappingService");
            
            StringBuilder pathContent = new StringBuilder();
            pathContent.append("# Personalized Learning Path\n\n");
            
            pathContent.append("## Based on your career stage: ").append(profile.getCareerStage()).append("\n\n");
            
            pathContent.append("## Building on your skills:\n");
            if (!profile.getSkills().isEmpty()) {
                for (String skill : profile.getSkills()) {
                    pathContent.append("- ").append(skill).append("\n");
                }
            } else {
                pathContent.append("- No skills specified yet\n");
            }
            pathContent.append("\n");
            
            pathContent.append("## To achieve your goals: ").append(profile.getGoals()).append("\n\n");
            
            pathContent.append("## Weekly commitment: ").append(profile.getHoursPerWeek()).append(" hours\n\n");
            
            pathContent.append("## Detailed Weekly Learning Plan\n\n");
            
            // Check if weeklyPlans is null or empty and handle accordingly
            if (pathData != null && pathData.getWeeklyPlans() != null && !pathData.getWeeklyPlans().isEmpty()) {
                logger.debug("Processing {} weekly plans", pathData.getWeeklyPlans().size());
                
                for (WeekPlan week : pathData.getWeeklyPlans()) {
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
                        pathContent.append(week.getSkill() != null ? week.getSkill() : "Skill Development").append("\n\n");
                        pathContent.append(week.getDescription() != null ? week.getDescription() : "Skill development week").append("\n\n");
                        pathContent.append("**Hours allocated:** ").append(week.getHoursAllocated()).append("\n\n");
                    }
                    
                    if (week.getResources() != null && !week.getResources().isEmpty()) {
                        pathContent.append("**Learning Resources:**\n");
                        for (VideoResource resource : week.getResources()) {
                            pathContent.append("- [").append(resource.getTitle()).append("](")
                                     .append(resource.getUrl()).append(")");
                            // Add description only if present
                            if (resource.getDescription() != null && !resource.getDescription().isEmpty()) {
                                pathContent.append(": ").append(resource.getDescription());
                            }
                            pathContent.append("\n");
                        }
                        pathContent.append("\n");
                    }
                    
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
                logger.warn("No weekly plans found in pathData, using fallback plan");
                pathContent.append("1. Foundations (Week 1-2)\n");
                pathContent.append("2. Core Skills (Week 3-5)\n");
                pathContent.append("3. Advanced Topics (Week 6-8)\n");
                pathContent.append("4. Project Work (Week 9-12)\n");
            }
            
            path.setPathContent(pathContent.toString());
            logger.info("Learning path generated successfully");
            return path;
            
        } catch (Exception e) {
            logger.error("Error generating learning path: {}", e.getMessage(), e);
            
            // Create a fallback learning path with error information
            LearningPath fallbackPath = new LearningPath();
            fallbackPath.setCreatedDate(LocalDate.now());
            
            StringBuilder errorContent = new StringBuilder();
            errorContent.append("# Learning Path Generation Error\n\n");
            errorContent.append("We encountered an error while generating your learning path.\n\n");
            errorContent.append("Error: ").append(e.getMessage()).append("\n\n");
            errorContent.append("Please try again later or contact support if the issue persists.\n\n");
            errorContent.append("## Default Learning Path\n\n");
            errorContent.append("1. Foundations (Week 1-2)\n");
            errorContent.append("2. Core Skills (Week 3-5)\n");
            errorContent.append("3. Advanced Topics (Week 6-8)\n");
            errorContent.append("4. Project Work (Week 9-12)\n");
            
            fallbackPath.setPathContent(errorContent.toString());
            return fallbackPath;
        }
    }
}