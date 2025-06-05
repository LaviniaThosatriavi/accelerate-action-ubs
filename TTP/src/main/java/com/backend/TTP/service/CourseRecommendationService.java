package com.backend.TTP.service;

import com.backend.TTP.dto.CourseReferenceDTO;
import com.backend.TTP.model.LearningPath;
import com.backend.TTP.model.User;
import com.backend.TTP.model.UserProfile;
import com.backend.TTP.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CourseRecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseRecommendationService.class);
    
    @Autowired
    private ExternalCourseService externalCourseService;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private Map<String, List<String>> categoryKeywordMap;
    
    /**
     * Extract learning categories from user profile
     */
    public List<String> extractLearningCategories(User user) {
        logger.info("Extracting learning categories for user: {}", user.getUsername());
        
        // NULL SAFETY: Handle case when user profile doesn't exist
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser(user);
        
        if (!profileOpt.isPresent()) {
            logger.warn("No profile found for user: {}, using default category", user.getUsername());
            return Collections.singletonList("COMPUTER_SCIENCE"); // Default category
        }
        
        UserProfile profile = profileOpt.get();
        Set<String> categories = new HashSet<>();
        
        // Extract categories from learning path
        LearningPath learningPath = profile.getLearningPath();
        if (learningPath != null && learningPath.getPathContent() != null) {
            String pathContent = learningPath.getPathContent().toUpperCase();
            logger.info("Processing learning path content");
            
            // Extract from goals
            String goals = profile.getGoals() != null ? profile.getGoals().toUpperCase() : "";
            
            // Extract weekly topics using regex pattern
            Pattern weekPattern = Pattern.compile("### Week \\d+: ([\\w/]+)");
            Matcher matcher = weekPattern.matcher(pathContent);
            
            // Add each weekly topic as a category
            while (matcher.find()) {
                String weeklyTopic = matcher.group(1).trim();
                logger.info("Found weekly topic: {}", weeklyTopic);
                categories.add(weeklyTopic);
            }
            
            // Process the learning path content against keyword mappings
            for (Map.Entry<String, List<String>> entry : categoryKeywordMap.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();
                
                // Check if any of the keywords are present in the path content or goals
                boolean matchFound = keywords.stream()
                        .anyMatch(keyword -> 
                            pathContent.contains(keyword) || goals.contains(keyword));
                
                if (matchFound) {
                    logger.info("Adding category based on keywords: {}", category);
                    categories.add(category);
                }
            }
        } else {
            logger.warn("No learning path or path content found for user: {}", user.getUsername());
        }
        
        // Extract from skills
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            logger.info("Processing skills: {}", profile.getSkills());
            for (String skill : profile.getSkills()) {
                if (skill != null) {
                    String upperSkill = skill.toUpperCase();
                    
                    // Check each skill against keyword mappings
                    for (Map.Entry<String, List<String>> entry : categoryKeywordMap.entrySet()) {
                        String category = entry.getKey();
                        List<String> keywords = entry.getValue();
                        
                        // Check if any of the keywords are present in the skill
                        boolean matchFound = keywords.stream()
                                .anyMatch(keyword -> upperSkill.contains(keyword));
                        
                        if (matchFound) {
                            logger.info("Adding category based on skill '{}': {}", skill, category);
                            categories.add(category);
                        }
                    }
                    
                    // Direct skill mapping (add the skill itself as a category if appropriate)
                    if (upperSkill.equals("BLOCKCHAIN") || upperSkill.equals("ETHEREUM") || 
                        upperSkill.equals("SOLIDITY") || upperSkill.equals("WEB3") || 
                        upperSkill.equals("JAVASCRIPT") || upperSkill.equals("PYTHON")) {
                        categories.add(upperSkill);
                    }
                }
            }
        } else {
            logger.warn("No skills found for user: {}", user.getUsername());
        }
        
        // Process the goals for more context
        if (profile.getGoals() != null) {
            String upperGoals = profile.getGoals().toUpperCase();
            
            if (upperGoals.contains("DECENTRALIZED") || upperGoals.contains("DAPP")) {
                categories.add("BLOCKCHAIN");
                categories.add("WEB3");
                categories.add("SMART_CONTRACTS");
            }
            
            if (upperGoals.contains("WEB") || upperGoals.contains("FRONTEND") || 
                upperGoals.contains("FRONT-END")) {
                categories.add("WEB_DEVELOPMENT");
            }
            
            // More goal-based mappings can be added here
        }
        
        // If no categories were extracted, add a default one
        if (categories.isEmpty()) {
            logger.info("No categories extracted, using default for user: {}", user.getUsername());
            categories.add("COMPUTER_SCIENCE");
        }
        
        // Sort by relevance (implicit priority by keeping order)
        List<String> prioritizedCategories = new ArrayList<>(categories);
        
        // Log the final categories
        logger.info("Final categories for user {}: {}", user.getUsername(), prioritizedCategories);
        return prioritizedCategories;
    }
    
    /**
     * Get course references for user based on their profile
     */
    public List<CourseReferenceDTO> getCourseReferencesForUser(User user) {
        logger.info("Getting course references for user: {}", user.getUsername());
        
        // NULL SAFETY: Handle null user
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        List<String> categories = extractLearningCategories(user);
        List<CourseReferenceDTO> references = new ArrayList<>();
        
        // Set to track categories we've already processed to avoid duplicates
        Set<String> processedCategories = new HashSet<>();
        
        for (String category : categories) {
            // Skip if we've already processed this category
            if (processedCategories.contains(category)) {
                continue;
            }
            
            processedCategories.add(category);
            
            CourseReferenceDTO reference = new CourseReferenceDTO();
            reference.setCategory(category);
            reference.setCourseraUrl(externalCourseService.getCourseraUrlForCategory(category));
            reference.setFreeCodeCampPlaylistUrl(externalCourseService.getFreeCodeCampPlaylistForCategory(category));
            
            // Get suggested videos
            String[] videos = externalCourseService.getFreeCodeCampVideosForCategory(category);
            
            // NULL SAFETY: Convert array to list safely
            List<String> videoList = videos != null ? Arrays.asList(videos) : new ArrayList<>();
            reference.setSuggestedVideos(videoList);
            
            references.add(reference);
            
            // Limit to a reasonable number of categories
            if (references.size() >= 5) {
                break;
            }
        }
        
        logger.info("Returning {} course references for user: {}", references.size(), user.getUsername());
        return references;
    }
}