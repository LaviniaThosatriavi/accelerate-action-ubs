package com.backend.TTP.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.backend.TTP.model.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;

@Service
public class SkillMappingService {
    private static final Logger logger = LoggerFactory.getLogger(SkillMappingService.class);

    private final RestTemplate restTemplate;
    private final String youtubeApiKey;
    private Map<String, SkillInfo> skillDatabase = new HashMap<>();
    private Map<String, CareerPath> careerPathMap = new HashMap<>();
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";
    
    // Cache for YouTube API responses to avoid rate limiting and improve performance
    private Map<String, List<VideoResource>> videoCache = new ConcurrentHashMap<>();

    public SkillMappingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.youtubeApiKey = "AIzaSyD55X2dxm5vgh6XWf5EffTIMhj2SZNxIME";
    }

    @PostConstruct
    private void initializeSkillDatabase() throws IOException {
        try {
            logger.info("Initializing skill database...");
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("skill_database.json");
            
            if (!resource.exists()) {
                logger.error("skill_database.json not found in classpath");
                throw new IOException("skill_database.json not found");
            }
            
            SkillDatabase database = mapper.readValue(resource.getInputStream(), SkillDatabase.class);
            
            if (database.getSkills() == null || database.getSkills().isEmpty()) {
                logger.warn("No skills found in database");
            } else {
                logger.info("Loaded {} skills from database", database.getSkills().size());
                database.getSkills().forEach(skill -> 
                    skillDatabase.put(skill.getName().toLowerCase(), skill));
            }
            
            if (database.getCareerPaths() == null || database.getCareerPaths().isEmpty()) {
                logger.warn("No career paths found in database");
            } else {
                logger.info("Loaded {} career paths from database", database.getCareerPaths().size());
                database.getCareerPaths().forEach(path -> 
                    careerPathMap.put(path.getName().toLowerCase(), path));
            }
            
            logger.info("Skill database initialization completed");
        } catch (Exception e) {
            logger.error("Failed to initialize skill database: {}", e.getMessage(), e);
            throw e;
        }
    }

    public LearningPathData generateLearningPath(UserProfile profile) {
        logger.info("Generating learning path for user with career stage: {}", profile.getCareerStage());
        
        List<String> userSkills = normalizeSkills(profile.getSkills());
        String careerStage = profile.getCareerStage().toLowerCase();
        String goals = profile.getGoals().toLowerCase();
        int hoursPerWeek = profile.getHoursPerWeek();

        CareerPath targetCareer = findOptimalCareerPath(careerStage, goals, userSkills);
        logger.info("Identified target career path: {}", targetCareer.getName());
        
        List<String> requiredSkills = calculateRequiredSkills(targetCareer, goals, userSkills);
        logger.info("Calculated {} required skills", requiredSkills.size());
        
        List<SkillGap> skillGaps = identifySkillGaps(userSkills, requiredSkills);
        List<SkillGap> prioritizedGaps = prioritizeSkillGaps(skillGaps, targetCareer, goals);
        logger.info("Identified and prioritized {} skill gaps", prioritizedGaps.size());
        
        int totalWeeks = calculateAdaptiveDuration(hoursPerWeek, prioritizedGaps);
        logger.info("Calculated learning path duration: {} weeks", totalWeeks);
        
        return buildLearningPath(prioritizedGaps, hoursPerWeek, totalWeeks, goals, targetCareer);
    }

    // Skill Normalization
    private List<String> normalizeSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            logger.warn("Empty skills list provided, returning empty list");
            return new ArrayList<>();
        }
        
        return skills.stream()
            .map(String::toLowerCase)
            .map(this::findBestSkillMatch)
            .collect(Collectors.toList());
    }

    private String findBestSkillMatch(String skill) {
        return skillDatabase.keySet().stream()
            .max(Comparator.comparingDouble(dbSkill -> 
                calculateSkillSimilarity(dbSkill, skill)))
            .filter(match -> calculateSkillSimilarity(match, skill) > 0.65)
            .orElse(skill);
    }

    private double calculateSkillSimilarity(String a, String b) {
        Set<String> aWords = new HashSet<>(Arrays.asList(a.split("[^a-zA-Z0-9]")));
        Set<String> bWords = new HashSet<>(Arrays.asList(b.split("[^a-zA-Z0-9]")));
        
        Set<String> intersection = new HashSet<>(aWords);
        intersection.retainAll(bWords);
        
        return (2.0 * intersection.size()) / (aWords.size() + bWords.size());
    }

    // Career Path Detection
    private CareerPath findOptimalCareerPath(String careerInput, String goals, List<String> userSkills) {
        Map<CareerPath, Double> careerScores = new HashMap<>();
        double maxScore = 0.0;
        CareerPath bestMatch = null;

        for (CareerPath career : careerPathMap.values()) {
            double score = calculateCareerMatchScore(career, goals, userSkills, careerInput);
            careerScores.put(career, score);
            if (score > maxScore) {
                maxScore = score;
                bestMatch = career;
            }
        }
        
        // If score is too low or no match found, create a synthetic career path
        if (maxScore < 0.3 || bestMatch == null) {
            logger.info("No good career match found, creating synthetic career path");
            return createSyntheticCareer(goals, userSkills);
        }
        
        logger.info("Found optimal career path: {} with score {}", bestMatch.getName(), maxScore);
        return bestMatch;
    }

    private double calculateCareerMatchScore(CareerPath career, String goals, 
                                          List<String> userSkills, String careerInput) {
        double score = 0.0;
        score += calculateNameMatchScore(career.getName(), careerInput) * 0.25;
        score += calculateKeywordOverlap(goals, career.getSkills()) * 0.4;
        score += calculateSkillMatchScore(userSkills, career.getSkills()) * 0.25;
        score += calculateRelatedSkillPotential(userSkills, career.getSkills()) * 0.1;
        return score;
    }

    private CareerPath createSyntheticCareer(String goals, List<String> userSkills) {
        CareerPath synthetic = new CareerPath();
        
        // Create a custom name based on goals and skills
        Set<String> keywords = new HashSet<>(extractGoalKeywords(goals));
        String careerName = "Custom";
        
        if (keywords.stream().anyMatch(k -> k.contains("blockchain") || k.contains("ethereum") || k.contains("crypto"))) {
            careerName = "Blockchain Developer";
        } else if (keywords.stream().anyMatch(k -> k.contains("frontend") || k.contains("ui") || k.contains("react"))) {
            careerName = "Frontend Developer";
        } else if (keywords.stream().anyMatch(k -> k.contains("backend") || k.contains("server") || k.contains("api"))) {
            careerName = "Backend Developer";
        } else if (keywords.stream().anyMatch(k -> k.contains("full") || k.contains("stack"))) {
            careerName = "Full Stack Developer";
        } else if (keywords.stream().anyMatch(k -> k.contains("data") || k.contains("machine") || k.contains("ai"))) {
            careerName = "Data Scientist";
        } else if (keywords.stream().anyMatch(k -> k.contains("mobile") || k.contains("android") || k.contains("ios"))) {
            careerName = "Mobile Developer";
        }
        
        synthetic.setName(careerName);
        synthetic.setCategory(extractCategory(careerName, goals));
        
        Set<String> skills = new LinkedHashSet<>();

        // Add skills based on goal keywords
        extractGoalKeywords(goals).forEach(keyword -> 
            skillDatabase.values().stream()
                .filter(skill -> skill.getName().toLowerCase().contains(keyword))
                .map(SkillInfo::getName)
                .forEach(skills::add)
        );

        // Add related skills from user's existing skills
        userSkills.forEach(skill -> {
            SkillInfo info = skillDatabase.get(skill.toLowerCase());
            if (info != null) skills.addAll(info.getRelatedSkills());
        });

        // Add fundamental skills
        skills.addAll(getFundamentalSkills(careerName, goals));
        synthetic.setSkills(new ArrayList<>(skills));
        
        logger.info("Created synthetic career path '{}' with {} skills", careerName, skills.size());
        return synthetic;
    }

    private String extractCategory(String careerName, String goals) {
        if (careerName.contains("Blockchain")) return "Blockchain";
        if (careerName.contains("Frontend")) return "Web Development";
        if (careerName.contains("Backend")) return "Web Development";
        if (careerName.contains("Full Stack")) return "Web Development";
        if (careerName.contains("Data")) return "Data Science";
        if (careerName.contains("Mobile")) return "Mobile Development";
        
        // Default based on goals
        if (goals.contains("web")) return "Web Development";
        if (goals.contains("data")) return "Data Science";
        if (goals.contains("mobile")) return "Mobile Development";
        if (goals.contains("game")) return "Game Development";
        
        return "Software Development";
    }

    private List<String> getFundamentalSkills(String careerName, String goals) {
        Set<String> fundamentals = new HashSet<>(Arrays.asList(
            "git", "problem solving", "debugging", "software design"
        ));
        
        // Add career-specific fundamentals
        if (careerName.contains("Blockchain")) {
            fundamentals.addAll(Arrays.asList(
                "javascript", "web3.js", "solidity", "ethereum", "smart contracts"
            ));
        } else if (careerName.contains("Frontend")) {
            fundamentals.addAll(Arrays.asList(
                "html", "css", "javascript", "responsive design", "ui/ux principles"
            ));
        } else if (careerName.contains("Backend")) {
            fundamentals.addAll(Arrays.asList(
                "databases", "api design", "authentication", "server management"
            ));
        } else if (careerName.contains("Full Stack")) {
            fundamentals.addAll(Arrays.asList(
                "html", "css", "javascript", "databases", "api design"
            ));
        } else if (careerName.contains("Data")) {
            fundamentals.addAll(Arrays.asList(
                "python", "statistics", "data visualization", "machine learning basics"
            ));
        } else if (careerName.contains("Mobile")) {
            fundamentals.addAll(Arrays.asList(
                "mobile ui design", "native apis", "app lifecycle management"
            ));
        }
        
        // Add goal-specific fundamentals
        if (goals.contains("ethereum") || goals.contains("blockchain") || goals.contains("web3")) {
            fundamentals.addAll(Arrays.asList(
                "blockchain fundamentals", "cryptography basics", "decentralized applications"
            ));
        }
        
        return new ArrayList<>(fundamentals);
    }

    // YouTube API integration
    public List<VideoResource> searchYouTubeVideos(String skill, String context, int maxResults) {
        String cacheKey = skill.toLowerCase() + ":" + context.toLowerCase();
        
        // Check cache first
        if (videoCache.containsKey(cacheKey)) {
            logger.info("Using cached videos for: {}", cacheKey);
            return videoCache.get(cacheKey);
        }
        
        // Set up retry mechanism
        int maxRetries = 3;
        int retryCount = 0;
        int delayMs = 1000;
        
        while (retryCount < maxRetries) {
            try {
                // Build specific query based on skill and context
                String query = skill;
                if (context != null && !context.isEmpty()) {
                    // Add context to make search more specific
                    if (context.contains("beginner") || context.contains("introduction")) {
                        query += " beginner tutorial";
                    } else if (context.contains("advanced") || context.contains("expert")) {
                        query += " advanced techniques";
                    } else if (context.contains("project")) {
                        query += " project tutorial";
                    } else {
                        query += " tutorial " + context;
                    }
                } else {
                    query += " programming tutorial";
                }
                
                logger.info("Searching YouTube for: {}", query);
                
                String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_URL)
                    .queryParam("part", "snippet")
                    .queryParam("maxResults", maxResults)
                    .queryParam("q", query)
                    .queryParam("type", "video")
                    .queryParam("relevanceLanguage", "en")
                    .queryParam("key", youtubeApiKey)
                    .build()
                    .toUriString();
                
                ResponseEntity<YouTubeSearchResponse> response = restTemplate.getForEntity(
                    url, YouTubeSearchResponse.class);
                
                if (response.getBody() != null && response.getBody().getItems() != null 
                    && !response.getBody().getItems().isEmpty()) {
                    
                    logger.info("Found {} YouTube videos for query: {}", 
                        response.getBody().getItems().size(), query);
                    
                    List<VideoResource> resources = response.getBody().getItems().stream()
                        .map(this::convertToResource)
                        .collect(Collectors.toList());
                    
                    // Cache the results
                    videoCache.put(cacheKey, resources);
                    
                    return resources;
                } else {
                    logger.warn("No YouTube videos found for query: {}", query);
                    retryCount++;
                    if (retryCount < maxRetries) {
                        logger.info("Retrying YouTube search ({}/{})", retryCount, maxRetries);
                        Thread.sleep(delayMs);
                        delayMs *= 2; // Exponential backoff
                    }
                }
            } catch (Exception e) {
                logger.error("Error searching YouTube: {}", e.getMessage(), e);
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        logger.info("Retrying YouTube search after error ({}/{})", retryCount, maxRetries);
                        Thread.sleep(delayMs);
                        delayMs *= 2; // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        // If we couldn't get videos from YouTube, generate dynamic placeholders
        List<VideoResource> fallbackVideos = generateDynamicFallbackVideos(skill, context);
        videoCache.put(cacheKey, fallbackVideos);
        return fallbackVideos;
    }
    
    private List<VideoResource> generateDynamicFallbackVideos(String skill, String context) {
        List<VideoResource> resources = new ArrayList<>();
        Random random = new Random();
        
        // Array of common educational channels
        String[] channels = {
            "Traversy Media", "freeCodeCamp", "The Net Ninja", "Programming with Mosh",
            "Academind", "Web Dev Simplified", "Fireship", "Coding Tech"
        };
        
        // Generate 3 fallback resources with different aspects of the skill
        VideoResource resource1 = new VideoResource();
        resource1.setTitle(skill + " Tutorial for Beginners");
        resource1.setVideoId("dQw4w9WgXcQ"); // Placeholder ID, would be different in real implementation
        resource1.setChannel(channels[random.nextInt(channels.length)]);
        resource1.setDescription("Learn " + skill + " fundamentals in this comprehensive tutorial");
        resources.add(resource1);
        
        VideoResource resource2 = new VideoResource();
        resource2.setTitle("Building Projects with " + skill);
        resource2.setVideoId("xvFZjo5PgG0"); // Placeholder ID
        resource2.setChannel(channels[random.nextInt(channels.length)]);
        resource2.setDescription("Practical project-based learning with " + skill);
        resources.add(resource2);
        
        VideoResource resource3 = new VideoResource();
        resource3.setTitle("Advanced " + skill + " Techniques");
        resource3.setVideoId("j5a0jTc9S10"); // Placeholder ID
        resource3.setChannel(channels[random.nextInt(channels.length)]);
        resource3.setDescription("Take your " + skill + " skills to the next level with advanced concepts");
        resources.add(resource3);
        
        return resources;
    }

    private VideoResource convertToResource(YouTubeItem item) {
        VideoResource resource = new VideoResource();
        resource.setTitle(item.getSnippet().getTitle());
        resource.setVideoId(item.getId().getVideoId());
        resource.setChannel(item.getSnippet().getChannelTitle());
        resource.setDescription(item.getSnippet().getDescription());
        return resource;
    }

    // Dynamic project idea generation
    private List<ProjectIdea> generateProjectIdeas(String skill, int weekNumber, String goals, String careerPath) {
        List<ProjectIdea> ideas = new ArrayList<>();
        
        // Determine difficulty level based on week number
        String level = weekNumber <= 2 ? "beginner" : 
                       weekNumber <= 4 ? "intermediate" : "advanced";
        
        // Generate project ideas dynamically based on skill, level, and career path
        ideas.add(generateProjectForSkillAndLevel(skill, level, goals, careerPath));
        
        // Add a second, more specific project if it's not the first week
        if (weekNumber > 1) {
            ideas.add(generateSpecializedProject(skill, level, goals, careerPath, weekNumber));
        }
        
        // For later weeks, add integration projects
        if (weekNumber > 3) {
            ideas.add(generateIntegrationProject(skill, goals, careerPath));
        }
        
        return ideas;
    }
    
    private ProjectIdea generateProjectForSkillAndLevel(String skill, String level, String goals, String careerPath) {
        ProjectIdea project = new ProjectIdea();
        
        // Base title and description on skill and level
        String title, description;
        
        if (level.equals("beginner")) {
            title = "Beginner " + skill + " Project";
            description = "Create a simple application using " + skill + " that demonstrates the basic concepts. " +
                         "Focus on understanding the fundamentals and getting comfortable with the syntax and structure.";
        } else if (level.equals("intermediate")) {
            title = "Intermediate " + skill + " Implementation";
            description = "Build a more complex application with " + skill + " that integrates multiple concepts. " +
                         "Focus on code organization, best practices, and adding more sophisticated features.";
        } else {
            title = "Advanced " + skill + " Project";
            description = "Develop a comprehensive application that showcases advanced " + skill + " techniques. " +
                         "Incorporate testing, optimization, and professional development workflows.";
        }
        
        // Enhance with career-specific context
        if (careerPath.toLowerCase().contains("blockchain")) {
            title = title + ": Blockchain Edition";
            description += " Incorporate blockchain concepts and decentralized architecture.";
        } else if (careerPath.toLowerCase().contains("frontend")) {
            title = title + ": UI/UX Focus";
            description += " Emphasize responsive design and user experience principles.";
        } else if (careerPath.toLowerCase().contains("backend")) {
            title = title + ": API Design";
            description += " Implement robust server-side architecture and API endpoints.";
        } else if (careerPath.toLowerCase().contains("full")) {
            title = title + ": Full Stack Implementation";
            description += " Create both frontend and backend components and ensure they work together seamlessly.";
        } else if (careerPath.toLowerCase().contains("data")) {
            title = title + ": Data Analysis";
            description += " Include data processing, visualization, and insights generation.";
        }
        
        // Add goal-specific customization
        if (goals.contains("blockchain") || goals.contains("ethereum") || goals.contains("decentralized")) {
            description += " Align the project with blockchain and decentralized application principles.";
        } else if (goals.contains("mobile") || goals.contains("app")) {
            description += " Ensure the application is mobile-friendly or consider creating a mobile version.";
        } else if (goals.contains("scalable") || goals.contains("enterprise")) {
            description += " Design with scalability and enterprise requirements in mind.";
        }
        
        project.setTitle(title);
        project.setDescription(description);
        project.setRequiredSkills(Collections.singletonList(skill));
        
        return project;
    }
    
    private ProjectIdea generateSpecializedProject(String skill, String level, String goals, String careerPath, int week) {
        ProjectIdea project = new ProjectIdea();
        
        // Generate a more specific project based on the skill domain and week number
        String domain = determineSkillDomain(skill, goals);
        String title, description;
        
        switch (domain) {
            case "frontend":
                String[] frontendProjects = {
                    "Interactive Dashboard", "Portfolio Website", "E-commerce Product Page", 
                    "Social Media Interface", "Data Visualization Tool"
                };
                title = skill + " " + frontendProjects[week % frontendProjects.length];
                description = "Create a " + frontendProjects[week % frontendProjects.length].toLowerCase() + 
                             " using " + skill + " with focus on responsive design and user experience.";
                break;
                
            case "backend":
                String[] backendProjects = {
                    "RESTful API", "Authentication Service", "Data Processing Pipeline",
                    "Caching Layer", "Database Integration"
                };
                title = skill + " " + backendProjects[week % backendProjects.length];
                description = "Implement a " + backendProjects[week % backendProjects.length].toLowerCase() + 
                             " using " + skill + " with focus on performance, security, and scalability.";
                break;
                
            case "blockchain":
                String[] blockchainProjects = {
                    "Smart Contract", "Token Implementation", "Decentralized Application",
                    "Wallet Interface", "NFT Marketplace"
                };
                title = skill + " " + blockchainProjects[week % blockchainProjects.length];
                description = "Develop a " + blockchainProjects[week % blockchainProjects.length].toLowerCase() + 
                             " using " + skill + " with focus on security, efficiency, and blockchain integration.";
                break;
                
            default:
                String[] generalProjects = {
                    "Utility Application", "Data Management Tool", "Automation Script",
                    "Integration Component", "Feature Implementation"
                };
                title = skill + " " + generalProjects[week % generalProjects.length];
                description = "Build a " + generalProjects[week % generalProjects.length].toLowerCase() + 
                             " using " + skill + " that solves a specific problem in your domain.";
        }
        
        // Add difficulty level to title
        title = level.substring(0, 1).toUpperCase() + level.substring(1) + " " + title;
        
        // Add goal-specific details
        if (goals.contains("portfolio") || goals.contains("job")) {
            description += " This project would make an excellent portfolio piece for job applications.";
        } else if (goals.contains("business") || goals.contains("startup")) {
            description += " Consider how this could be extended into a viable business solution.";
        } else if (goals.contains("learn") || goals.contains("skill")) {
            description += " Focus on learning and implementing best practices throughout the development process.";
        }
        
        project.setTitle(title);
        project.setDescription(description);
        List<String> requiredSkills = new ArrayList<>();
        requiredSkills.add(skill);
        
        // Add complementary skills based on domain
        if (domain.equals("frontend")) {
            requiredSkills.add("UI/UX");
            requiredSkills.add("responsive design");
        } else if (domain.equals("backend")) {
            requiredSkills.add("API design");
            requiredSkills.add("server management");
        } else if (domain.equals("blockchain")) {
            requiredSkills.add("smart contracts");
            requiredSkills.add("web3");
        }
        
        project.setRequiredSkills(requiredSkills);
        
        return project;
    }
    
    private ProjectIdea generateIntegrationProject(String skill, String goals, String careerPath) {
        ProjectIdea project = new ProjectIdea();
        
        String title = "Integration Project: " + skill + " with " + 
                      determineComplementarySkill(skill, goals, careerPath);
        
        String description = "Create a comprehensive project that combines " + skill + 
                            " with other technologies in your learning path. Focus on how different components " +
                            "interact and create a cohesive application.";
        
        // Add specific guidance based on career path
        if (careerPath.toLowerCase().contains("blockchain")) {
            description += " Demonstrate how traditional web technologies can interface with blockchain components.";
        } else if (careerPath.toLowerCase().contains("full")) {
            description += " Ensure seamless integration between frontend and backend components.";
        } else if (careerPath.toLowerCase().contains("data")) {
            description += " Show how data processing can feed into user-facing visualizations or reports.";
        }
        
        // Add goal-specific focus
        if (goals.contains("team") || goals.contains("collaboration")) {
            description += " Structure the project to simulate a team environment with clear component boundaries.";
        } else if (goals.contains("production") || goals.contains("deploy")) {
            description += " Include deployment considerations and production-ready features.";
        }
        
        project.setTitle(title);
        project.setDescription(description);
        
        // Determine related skills for this integration project
        List<String> relatedSkills = new ArrayList<>();
        relatedSkills.add(skill);
        relatedSkills.add(determineComplementarySkill(skill, goals, careerPath));
        
        project.setRequiredSkills(relatedSkills);
        
        return project;
    }
    
    private String determineSkillDomain(String skill, String goals) {
        skill = skill.toLowerCase();
        
        if (skill.contains("html") || skill.contains("css") || skill.contains("react") || 
            skill.contains("angular") || skill.contains("vue") || skill.contains("ui") || 
            skill.contains("design") || skill.contains("frontend")) {
            return "frontend";
        }
        
        if (skill.contains("api") || skill.contains("server") || skill.contains("node") || 
            skill.contains("express") || skill.contains("database") || skill.contains("sql") || 
            skill.contains("backend")) {
            return "backend";
        }
        
        if (skill.contains("blockchain") || skill.contains("ethereum") || skill.contains("solidity") || 
            skill.contains("web3") || skill.contains("smart") || skill.contains("crypto")) {
            return "blockchain";
        }
        
        if (skill.contains("data") || skill.contains("machine") || skill.contains("ai") || 
            skill.contains("analysis") || skill.contains("visualization")) {
            return "data";
        }
        
        if (skill.contains("mobile") || skill.contains("android") || skill.contains("ios") || 
            skill.contains("swift") || skill.contains("flutter") || skill.contains("react native")) {
            return "mobile";
        }
        
        // Check goals for additional context
        if (goals.contains("frontend") || goals.contains("ui") || goals.contains("design")) {
            return "frontend";
        }
        
        if (goals.contains("backend") || goals.contains("server") || goals.contains("api")) {
            return "backend";
        }
        
        if (goals.contains("blockchain") || goals.contains("ethereum") || goals.contains("decentralized")) {
            return "blockchain";
        }
        
        return "general";
    }
    
    private String determineComplementarySkill(String skill, String goals, String careerPath) {
        String domain = determineSkillDomain(skill, goals);
        
        // Return a complementary skill based on the domain
        switch (domain) {
            case "frontend":
                return "REST API Integration";
            case "backend":
                return "Frontend Framework";
            case "blockchain":
                return "Web Development";
            case "data":
                return "Data Visualization";
            case "mobile":
                return "Backend Services";
            default:
                return "Database Management";
        }
    }

    // Skill Requirements Calculation
    private List<String> calculateRequiredSkills(CareerPath career, String goals, 
                                              List<String> userSkills) {
        Set<String> requiredSkills = new LinkedHashSet<>(career.getSkills());
        
        career.getSkills().forEach(skill -> {
            SkillInfo info = skillDatabase.get(skill.toLowerCase());
            if (info != null) requiredSkills.addAll(info.getRelatedSkills());
        });

        extractGoalKeywords(goals).forEach(keyword -> 
            skillDatabase.values().stream()
                .filter(skill -> skill.getName().toLowerCase().contains(keyword))
                .map(SkillInfo::getName)
                .forEach(requiredSkills::add)
        );

        return new ArrayList<>(requiredSkills);
    }

    private int calculateAdaptiveDuration(int hoursPerWeek, List<SkillGap> gaps) {
        int totalHours = gaps.stream()
            .mapToInt(gap -> gap.getDifficulty() * 10) // Base hours per skill
            .sum();
        
        // Adjust based on available hours per week
        int minWeeks = 8; // Ensure a minimum learning path duration
        int calculatedWeeks = (int) Math.ceil((double) totalHours / hoursPerWeek);
        return Math.max(minWeeks, calculatedWeeks);
    }

    private void addSkillWeeks(List<WeekPlan> plan, SkillGap gap, int weeksAllocated, 
                             int hoursPerWeek, String goals, CareerPath career) {
        String skillName = gap.getName();
        int difficulty = gap.getDifficulty();
        
        // Create progression of learning for this skill across allocated weeks
        for (int i = 0; i < weeksAllocated; i++) {
            final int currentWeekNumber = i + 1;
            final int planWeekNumber = plan.size() + 1;
            
            WeekPlan week = new WeekPlan();
            week.setWeekNumber(planWeekNumber);
            week.setSkill(skillName);
            
            // Calculate how far into the skill learning we are (0.0 to 1.0)
            double progressRatio = (double) currentWeekNumber / weeksAllocated;
            
            // Set description based on progress
            if (progressRatio < 0.3) {
                week.setDescription("Introduction to " + skillName + ": Learn the fundamentals and basic concepts. " +
                                  "Focus on understanding the core principles and getting comfortable with " + skillName + ".");
            } else if (progressRatio < 0.7) {
                week.setDescription("Intermediate " + skillName + ": Build on your foundation with more complex topics. " +
                                  "Practice implementing " + skillName + " in practical scenarios and deepen your understanding.");
            } else {
                week.setDescription("Advanced " + skillName + ": Master advanced concepts and techniques. " +
                                  "Focus on best practices, optimization, and real-world applications of " + skillName + ".");
            }
            
            // Set hours allocated
            week.setHoursAllocated(hoursPerWeek);
            
            // Find appropriate YouTube videos
            String context = progressRatio < 0.3 ? "beginner" : 
                           progressRatio < 0.7 ? "intermediate" : "advanced";
            week.setResources(searchYouTubeVideos(skillName, context, 3));
            
            // Generate relevant project ideas
            week.setProjects(generateProjectIdeas(skillName, currentWeekNumber, goals, career.getName()));
            
            // Add review weeks periodically
            if (currentWeekNumber > 1 && currentWeekNumber % 3 == 0) {
                week.setReviewWeek(true);
                List<String> reviewSkills = new ArrayList<>();
                
                // Collect skills from previous weeks for review
                for (int j = Math.max(0, plan.size() - 2); j < plan.size(); j++) {
                    reviewSkills.add(plan.get(j).getSkill());
                }
                
                week.setReviewSkills(reviewSkills);
            }
            
            plan.add(week);
        }
    }

    private void addIntegrationWeeks(List<WeekPlan> plan, int remainingWeeks, 
                                   int hoursPerWeek, String goals, CareerPath career) {
        if (remainingWeeks <= 0) return;
        
        // Create a capstone project phase spanning the remaining weeks
        WeekPlan integrationWeek = new WeekPlan();
        integrationWeek.setWeekNumber(plan.size() + 1);
        integrationWeek.setSkill("Integration Project");
        integrationWeek.setDescription("Final project integrating all skills learned throughout the course. " +
                                     "Apply the full spectrum of your knowledge to create a comprehensive application " +
                                     "that showcases your abilities and aligns with your career goals.");
        integrationWeek.setHoursAllocated(hoursPerWeek * remainingWeeks);
        
        // Get all skills learned so far
        List<String> allSkills = plan.stream()
            .map(WeekPlan::getSkill)
            .distinct()
            .collect(Collectors.toList());
        
        // Generate capstone project ideas
        List<ProjectIdea> capstoneProjects = new ArrayList<>();
        
        ProjectIdea mainProject = new ProjectIdea();
        mainProject.setTitle("Capstone Project: " + career.getName() + " Portfolio");
        
        StringBuilder description = new StringBuilder();
        description.append("Create a comprehensive project that demonstrates all the skills you've learned. ");
        
        if (career.getName().toLowerCase().contains("blockchain")) {
            description.append("Build a fully functional decentralized application with smart contracts, ");
            description.append("backend services, and an intuitive user interface. ");
        } else if (career.getName().toLowerCase().contains("frontend")) {
            description.append("Develop a sophisticated frontend application with advanced UI/UX features, ");
            description.append("state management, and integration with backend services. ");
        } else if (career.getName().toLowerCase().contains("backend")) {
            description.append("Create a robust backend system with APIs, authentication, database integration, ");
            description.append("and deployment configuration. ");
        } else if (career.getName().toLowerCase().contains("full")) {
            description.append("Build a complete full-stack application with frontend, backend, database, ");
            description.append("authentication, and deployment pipeline. ");
        } else {
            description.append("Develop a project that showcases your technical skills and problem-solving abilities ");
            description.append("in a real-world context. ");
        }
        
        description.append("This project should serve as a centerpiece for your portfolio and demonstrate ");
        description.append("your readiness for professional opportunities in the field.");
        
        mainProject.setTitle("Capstone Project: " + career.getName() + " Portfolio");
        mainProject.setDescription(description.toString());
        mainProject.setRequiredSkills(allSkills);
        
        capstoneProjects.add(mainProject);
        
        // Add a second, more focused project option
        ProjectIdea alternativeProject = new ProjectIdea();
        alternativeProject.setTitle("Specialized Project: " + extractSpecialization(goals, career.getName()));
        
        StringBuilder altDescription = new StringBuilder();
        altDescription.append("Create a more focused project that emphasizes your specific interests in ");
        altDescription.append(extractSpecialization(goals, career.getName())).append(". ");
        altDescription.append("This project should highlight your expertise in the areas most relevant to your career goals.");
        
        alternativeProject.setDescription(altDescription.toString());
        alternativeProject.setRequiredSkills(allSkills.subList(0, Math.min(5, allSkills.size())));
        
        capstoneProjects.add(alternativeProject);
        
        integrationWeek.setProjects(capstoneProjects);
        plan.add(integrationWeek);
    }
    
    private String extractSpecialization(String goals, String careerName) {
        // Extract a specialization from goals or career name
        if (goals.contains("blockchain") || goals.contains("ethereum") || goals.contains("crypto")) {
            return "Blockchain Development";
        } else if (goals.contains("ui") || goals.contains("user interface") || goals.contains("design")) {
            return "UI/UX Design";
        } else if (goals.contains("api") || goals.contains("backend") || goals.contains("server")) {
            return "Backend Architecture";
        } else if (goals.contains("data") || goals.contains("analytics") || goals.contains("visualization")) {
            return "Data Visualization";
        } else if (goals.contains("mobile") || goals.contains("app") || goals.contains("responsive")) {
            return "Mobile Development";
        }
        
        // Default based on career name
        if (careerName.contains("Blockchain")) {
            return "Decentralized Applications";
        } else if (careerName.contains("Frontend")) {
            return "Interactive User Interfaces";
        } else if (careerName.contains("Backend")) {
            return "Scalable API Architecture";
        } else if (careerName.contains("Full Stack")) {
            return "End-to-End Application Development";
        } else if (careerName.contains("Data")) {
            return "Data Science Applications";
        }
        
        return "Software Architecture";
    }

    private LearningPathData createPathData(List<SkillGap> gaps, List<WeekPlan> plan, 
                                          int totalHours) {
        LearningPathData data = new LearningPathData();
        data.setTotalHours(totalHours);
        data.setTotalWeeks(plan.size());
        data.setWeeklyPlans(plan);
        return data;
    }

    // Skill Gap Analysis
    private List<SkillGap> identifySkillGaps(List<String> userSkills, List<String> requiredSkills) {
        return requiredSkills.stream()
            .map(String::toLowerCase)
            .filter(skill -> !userSkills.contains(skill))
            .map(skill -> skillDatabase.containsKey(skill) ? 
                createSkillGap(skillDatabase.get(skill)) : 
                createDefaultSkillGap(skill))
            .collect(Collectors.toList());
    }

    private SkillGap createDefaultSkillGap(String skillName) {
        SkillGap gap = new SkillGap();
        gap.setName(skillName);
        gap.setDescription("Essential skill for your goals");
        gap.setDifficulty(3);
        gap.setImportance(0.5);
        return gap;
    }

    // Skill Prioritization
    private List<SkillGap> prioritizeSkillGaps(List<SkillGap> gaps, CareerPath career, String goals) {
        gaps.forEach(gap -> {
            double careerImportance = career.getSkills().contains(gap.getName()) ? 0.6 : 0.2;
            double goalImportance = calculateGoalRelevance(gap.getName(), goals) * 0.4;
            gap.setImportance(careerImportance + goalImportance);
        });
        
        return gaps.stream()
            .sorted(Comparator.comparingDouble(SkillGap::getImportance).reversed())
            .limit(7)
            .collect(Collectors.toList());
    }

    // Learning Path Construction
    private LearningPathData buildLearningPath(List<SkillGap> gaps, int hours, int weeks, 
                                             String goals, CareerPath career) {
        List<WeekPlan> plan = new ArrayList<>();
        int[] remainingWeeksWrapper = new int[]{weeks}; // Use array to make effectively final
        
        Map<SkillGap, Integer> allocations = allocateWeeksToSkills(gaps, weeks);
        allocations.forEach((gap, weeksAllocated) -> {
            addSkillWeeks(plan, gap, weeksAllocated, hours, goals, career);
            remainingWeeksWrapper[0] -= weeksAllocated; // Modify array content
        });
        
        addIntegrationWeeks(plan, remainingWeeksWrapper[0], hours, goals, career);
        return createPathData(gaps, plan, hours * weeks);
    }

    private Map<SkillGap, Integer> allocateWeeksToSkills(List<SkillGap> gaps, int totalWeeks) {
        Map<SkillGap, Integer> allocations = new LinkedHashMap<>();
        double totalPriority = gaps.stream().mapToDouble(SkillGap::getImportance).sum();
        
        // Reserve some weeks for integration/capstone project
        int reservedWeeks = Math.min(4, totalWeeks / 4);
        int allocatableWeeks = totalWeeks - reservedWeeks;
        
        gaps.forEach(gap -> {
            double ratio = gap.getImportance() / totalPriority;
            int weeks = Math.max(1, (int) Math.round(allocatableWeeks * ratio));
            allocations.put(gap, weeks);
        });
        
        int allocatedTotal = allocations.values().stream().mapToInt(Integer::intValue).sum();
        if (allocatedTotal < allocatableWeeks) {
            // Distribute remaining weeks to highest priority gaps
            int remaining = allocatableWeeks - allocatedTotal;
            for (int i = 0; i < remaining && i < gaps.size(); i++) {
                SkillGap gap = gaps.get(i);
                allocations.put(gap, allocations.get(gap) + 1);
            }
        } else if (allocatedTotal > allocatableWeeks) {
            // Remove weeks from lowest priority gaps
            int excess = allocatedTotal - allocatableWeeks;
            for (int i = gaps.size() - 1; i >= 0 && excess > 0; i--) {
                SkillGap gap = gaps.get(i);
                int current = allocations.get(gap);
                if (current > 1) {
                    allocations.put(gap, current - 1);
                    excess--;
                }
            }
        }
        
        return allocations;
    }

    // Helper Methods
    private double calculateNameMatchScore(String careerName, String input) {
        String[] inputWords = input.toLowerCase().split("\\W+");
        String[] careerWords = careerName.toLowerCase().split("\\W+");
        
        Set<String> intersection = new HashSet<>(Arrays.asList(inputWords));
        intersection.retainAll(Arrays.asList(careerWords));
        
        return (double) intersection.size() / careerWords.length;
    }

    private double calculateKeywordOverlap(String goals, List<String> skills) {
        Set<String> goalWords = new HashSet<>(extractGoalKeywords(goals));
        return (double) skills.stream()
            .filter(skill -> goalWords.stream().anyMatch(skill::contains))
            .count() / skills.size();
    }

    private List<String> extractGoalKeywords(String goals) {
        return Arrays.stream(goals.split("\\W+"))
            .filter(word -> word.length() > 3)
            .collect(Collectors.toList());
    }

    private double calculateSkillMatchScore(List<String> userSkills, List<String> careerSkills) {
        Set<String> userSet = new HashSet<>(userSkills);
        return (double) careerSkills.stream()
            .filter(skill -> userSet.contains(skill.toLowerCase()))
            .count() / careerSkills.size();
    }

    private double calculateRelatedSkillPotential(List<String> userSkills, List<String> careerSkills) {
        return careerSkills.stream()
            .mapToDouble(skill -> {
                SkillInfo info = skillDatabase.get(skill.toLowerCase());
                return info != null ? 
                    calculateSkillProximity(userSkills, info.getRelatedSkills()) : 0;
            })
            .average()
            .orElse(0);
    }

    private double calculateSkillProximity(List<String> userSkills, List<String> relatedSkills) {
        Set<String> userSet = new HashSet<>(userSkills);
        return (double) relatedSkills.stream()
            .filter(userSet::contains)
            .count() / relatedSkills.size();
    }

    private double calculateGoalRelevance(String skill, String goals) {
        return extractGoalKeywords(goals).stream()
            .filter(keyword -> skill.toLowerCase().contains(keyword))
            .count() * 0.15;
    }

    private SkillGap createSkillGap(SkillInfo skill) {
        SkillGap gap = new SkillGap();
        gap.setName(skill.getName());
        gap.setDescription(skill.getDescription());
        gap.setDifficulty(skill.getDifficulty());
        return gap;
    }

    // DTO Classes
    public static class LearningPathData {
        private int totalHours;
        private int totalWeeks;
        private List<WeekPlan> weeklyPlans;

        // Getters and setters
        public int getTotalHours() { return totalHours; }
        public void setTotalHours(int totalHours) { this.totalHours = totalHours; }
        public int getTotalWeeks() { return totalWeeks; }
        public void setTotalWeeks(int totalWeeks) { this.totalWeeks = totalWeeks; }
        public List<WeekPlan> getWeeklyPlans() { return weeklyPlans; }
        public void setWeeklyPlans(List<WeekPlan> weeklyPlans) { this.weeklyPlans = weeklyPlans; }
    }

    public static class WeekPlan {
        private int weekNumber;
        private String skill;
        private String description;
        private int hoursAllocated;
        private List<VideoResource> resources;
        private List<ProjectIdea> projects;
        private boolean reviewWeek;
        private List<String> reviewSkills;

        // Getters and setters
        public int getWeekNumber() { return weekNumber; }
        public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }
        public String getSkill() { return skill; }
        public void setSkill(String skill) { this.skill = skill; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getHoursAllocated() { return hoursAllocated; }
        public void setHoursAllocated(int hoursAllocated) { this.hoursAllocated = hoursAllocated; }
        public List<VideoResource> getResources() { return resources; }
        public void setResources(List<VideoResource> resources) { this.resources = resources; }
        public List<ProjectIdea> getProjects() { return projects; }
        public void setProjects(List<ProjectIdea> projects) { this.projects = projects; }
        public boolean isReviewWeek() { return reviewWeek; }
        public void setReviewWeek(boolean reviewWeek) { this.reviewWeek = reviewWeek; }
        public List<String> getReviewSkills() { return reviewSkills; }
        public void setReviewSkills(List<String> reviewSkills) { this.reviewSkills = reviewSkills; }
    }

    public static class VideoResource {
        private String title;
        private String videoId;
        private String channel;
        private String description;

        public String getUrl() {
            return "https://www.youtube.com/watch?v=" + videoId;
        }

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ProjectIdea {
        private String title;
        private String description;
        private List<String> requiredSkills;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    }

    public static class SkillGap {
        private String name;
        private String description;
        private int difficulty;
        private double importance;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getDifficulty() { return difficulty; }
        public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
        public double getImportance() { return importance; }
        public void setImportance(double importance) { this.importance = importance; }
    }

    public static class SkillDatabase {
        private List<SkillInfo> skills;
        private List<CareerPath> careerPaths;
        
        // Getters and setters
        public List<SkillInfo> getSkills() { return skills; }
        public void setSkills(List<SkillInfo> skills) { this.skills = skills; }
        public List<CareerPath> getCareerPaths() { return careerPaths; }
        public void setCareerPaths(List<CareerPath> careerPaths) { this.careerPaths = careerPaths; }
    }

    public static class SkillInfo {
        private String name;
        private String description;
        private List<String> tags;
        private int difficulty;
        private List<String> relatedSkills;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public int getDifficulty() { return difficulty; }
        public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
        public List<String> getRelatedSkills() { return relatedSkills; }
        public void setRelatedSkills(List<String> relatedSkills) { this.relatedSkills = relatedSkills; }
    }

    public static class CareerPath {
        private String name;
        private List<String> skills;
        private String category;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    // YouTube API Response Handling
    private static class YouTubeSearchResponse {
        private List<YouTubeItem> items;

        public List<YouTubeItem> getItems() { return items; }
        public void setItems(List<YouTubeItem> items) { this.items = items; }
    }

    private static class YouTubeItem {
        private YouTubeId id;
        private YouTubeSnippet snippet;

        public YouTubeId getId() { return id; }
        public void setId(YouTubeId id) { this.id = id; }
        public YouTubeSnippet getSnippet() { return snippet; }
        public void setSnippet(YouTubeSnippet snippet) { this.snippet = snippet; }
    }

    private static class YouTubeId {
        private String videoId;

        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
    }

    private static class YouTubeSnippet {
        private String title;
        private String channelTitle;
        private String description;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getChannelTitle() { return channelTitle; }
        public void setChannelTitle(String channelTitle) { this.channelTitle = channelTitle; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}