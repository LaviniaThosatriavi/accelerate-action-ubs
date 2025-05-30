package com.backend.TTP.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.backend.TTP.model.UserProfile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;



@Service
public class SkillMappingService {

    private final RestTemplate restTemplate;
    private final String youtubeApiKey;
    
    // In-memory skill database
    private Map<String, SkillInfo> skillDatabase = new HashMap<>();
    
    // Career path recommendations
    private Map<String, List<String>> careerPaths = new HashMap<>();

    public SkillMappingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.youtubeApiKey = "AIzaSyCpzC5khNPGXTYgOxysP7if2uloHNft8ds";
        
        // Initialize the skill database and career paths
        initializeSkillDatabase();
    }

    @PostConstruct
    private void initializeSkillDatabase() {
        try {
            // Load skill data from a JSON file in resources
            // This could be externalized to a database in production
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("skill_database.json");
            SkillDatabase database = mapper.readValue(resource.getInputStream(), SkillDatabase.class);
            
            // Populate the skill database
            database.getSkills().forEach(skill -> 
                skillDatabase.put(skill.getName().toLowerCase(), skill));
                
            // Populate career paths
            database.getCareerPaths().forEach(path -> 
                careerPaths.put(path.getName().toLowerCase(), path.getSkills()));
                
        } catch (IOException e) {
            // Fallback to hardcoded data if file loading fails
            initializeDefaultSkillData();
        }
    }
    
    private void initializeDefaultSkillData() {
        // Basic skill information
        addSkill("java", "A general-purpose programming language", 
                Arrays.asList("programming", "backend"), 4, 
                Arrays.asList("spring", "hibernate", "maven"));
                
        addSkill("spring", "Java application framework", 
                Arrays.asList("java", "framework", "backend"), 4, 
                Arrays.asList("spring boot", "spring security", "spring data"));
                
        addSkill("sql", "Database query language", 
                Arrays.asList("database", "data"), 3, 
                Arrays.asList("postgresql", "mysql", "database design"));
                
        addSkill("react", "JavaScript library for UI", 
                Arrays.asList("frontend", "javascript"), 3, 
                Arrays.asList("redux", "javascript", "web development"));
                
        addSkill("python", "General-purpose programming language", 
                Arrays.asList("programming", "data science"), 3, 
                Arrays.asList("django", "flask", "pandas"));
                
        // More skills...
        
        // Career paths with required skills
        careerPaths.put("junior backend developer", 
                Arrays.asList("java", "spring", "sql", "git", "rest api"));
                
        careerPaths.put("mid-level backend developer", 
                Arrays.asList("design patterns", "microservices", "spring security", "docker", "jpa"));
                
        careerPaths.put("senior backend developer", 
                Arrays.asList("system design", "kubernetes", "ci/cd", "performance optimization", "architecture"));
                
        careerPaths.put("junior frontend developer", 
                Arrays.asList("html", "css", "javascript", "react", "responsive design"));
                
        careerPaths.put("data scientist", 
                Arrays.asList("python", "pandas", "sklearn", "tensorflow", "statistics"));
                
        // More career paths...
    }
    
    private void addSkill(String name, String description, List<String> tags, 
                         int difficulty, List<String> relatedSkills) {
        SkillInfo skill = new SkillInfo();
        skill.setName(name);
        skill.setDescription(description);
        skill.setTags(tags);
        skill.setDifficulty(difficulty);
        skill.setRelatedSkills(relatedSkills);
        
        skillDatabase.put(name.toLowerCase(), skill);
    }
    
    public List<String> normalizeSkills(List<String> inputSkills) {
        if (inputSkills == null || inputSkills.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> normalized = new ArrayList<>();
        
        for (String input : inputSkills) {
            String lowerInput = input.toLowerCase();
            
            // Direct match
            if (skillDatabase.containsKey(lowerInput)) {
                normalized.add(lowerInput);
                continue;
            }
            
            // Try to find closest match
            String bestMatch = findClosestSkill(lowerInput);
            if (bestMatch != null) {
                normalized.add(bestMatch);
            } else {
                // If no match, keep the original (we'll treat it as a new skill)
                normalized.add(lowerInput);
                
                // Optionally add it to our database
                addNewSkillToDatabase(lowerInput);
            }
        }
        
        return normalized;
    }
    
    private String findClosestSkill(String input) {
        String bestMatch = null;
        double bestSimilarity = 0.3; // Threshold for accepting a match
        
        for (String skillName : skillDatabase.keySet()) {
            double similarity = calculateSimilarity(input, skillName);
            
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestMatch = skillName;
            }
        }
        
        return bestMatch;
    }
    
    private double calculateSimilarity(String s1, String s2) {
        // Simple Jaccard similarity for strings
        Set<Character> set1 = s1.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        Set<Character> set2 = s2.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        
        Set<Character> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<Character> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return (double) intersection.size() / union.size();
    }
    
    private void addNewSkillToDatabase(String skillName) {
        // Add a basic entry for a new skill
        SkillInfo newSkill = new SkillInfo();
        newSkill.setName(skillName);
        newSkill.setDescription("User-defined skill");
        newSkill.setTags(Collections.emptyList());
        newSkill.setDifficulty(3); // Medium difficulty by default
        newSkill.setRelatedSkills(Collections.emptyList());
        
        skillDatabase.put(skillName.toLowerCase(), newSkill);
    }
    
    public List<SkillGap> findSkillGaps(List<String> userSkills, String careerStage, String goals) {
        List<String> normalizedSkills = normalizeSkills(userSkills);
        Set<String> userSkillSet = new HashSet<>(normalizedSkills);
        
        // Determine required skills based on career stage
        List<String> requiredSkills = getRequiredSkillsForCareer(careerStage);
        
        // Extract skills from goals
        List<String> goalSkills = extractSkillsFromGoals(goals);
        
        // Combine all required skills
        Set<String> allRequiredSkills = new HashSet<>(requiredSkills);
        allRequiredSkills.addAll(goalSkills);
        
        // Identify gaps
        List<SkillGap> gaps = new ArrayList<>();
        
        for (String skill : allRequiredSkills) {
            if (!userSkillSet.contains(skill) && skillDatabase.containsKey(skill)) {
                SkillInfo info = skillDatabase.get(skill);
                
                double importance = calculateImportance(skill, careerStage, goals);
                
                SkillGap gap = new SkillGap();
                gap.setName(skill);
                gap.setDescription(info.getDescription());
                gap.setImportance(importance);
                gap.setDifficulty(info.getDifficulty());
                
                gaps.add(gap);
            }
        }
        
        // Sort by importance
        gaps.sort(Comparator.comparing(SkillGap::getImportance).reversed());
        
        return gaps;
    }
    
    private List<String> getRequiredSkillsForCareer(String careerStage) {
        // Handle the case when career stage isn't found
        if (careerStage == null || careerStage.isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerCareer = careerStage.toLowerCase();
        
        // Look for exact match
        if (careerPaths.containsKey(lowerCareer)) {
            return careerPaths.get(lowerCareer);
        }
        
        // Look for partial match
        for (Map.Entry<String, List<String>> entry : careerPaths.entrySet()) {
            if (lowerCareer.contains(entry.getKey()) || entry.getKey().contains(lowerCareer)) {
                return entry.getValue();
            }
        }
        
        // If no match, extract key terms and try to infer
        List<String> inferredSkills = new ArrayList<>();
        
        // Detect backend focus
        if (lowerCareer.contains("back") || lowerCareer.contains("java") || 
            lowerCareer.contains("server") || lowerCareer.contains("api")) {
            inferredSkills.addAll(careerPaths.get("junior backend developer"));
        } 
        // Detect frontend focus
        else if (lowerCareer.contains("front") || lowerCareer.contains("web") || 
                lowerCareer.contains("ui") || lowerCareer.contains("design")) {
            inferredSkills.addAll(careerPaths.get("junior frontend developer"));
        }
        // Detect data focus
        else if (lowerCareer.contains("data") || lowerCareer.contains("analy") || 
                lowerCareer.contains("science") || lowerCareer.contains("ml")) {
            inferredSkills.addAll(Arrays.asList("python", "sql", "data analysis", "statistics"));
        }
        // Generic software development skills
        else {
            inferredSkills.addAll(Arrays.asList("programming", "git", "problem-solving", "algorithms"));
        }
        
        return inferredSkills;
    }
    
    private List<String> extractSkillsFromGoals(String goals) {
        if (goals == null || goals.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> extractedSkills = new ArrayList<>();  // Define extractedSkills
        String lowerGoals = goals.toLowerCase();  // Define lowerGoals
        
        // Look for skill mentions in the goals
        for (String skill : skillDatabase.keySet()) {
            if (lowerGoals.contains(skill)) {
                extractedSkills.add(skill);
            }
        }
        
        // Look for career mentions in the goals
        for (Map.Entry<String, List<String>> entry : careerPaths.entrySet()) {
            if (lowerGoals.contains(entry.getKey())) {
                extractedSkills.addAll(entry.getValue());
                break; // Only use one career path to avoid overwhelming with skills
            }
        }
        
        // Detect key areas of interest
        Map<String, List<String>> keyAreas = new HashMap<>();
        keyAreas.put("backend", Arrays.asList("java", "spring", "api", "server"));
        keyAreas.put("frontend", Arrays.asList("javascript", "react", "ui", "web"));
        keyAreas.put("data", Arrays.asList("python", "analysis", "science", "machine learning"));
        keyAreas.put("devops", Arrays.asList("docker", "kubernetes", "deployment", "cloud"));
        
        for (Map.Entry<String, List<String>> area : keyAreas.entrySet()) {
            for (String keyword : area.getValue()) {
                if (lowerGoals.contains(keyword)) {
                    extractedSkills.addAll(getRequiredSkillsForCareer(area.getKey() + " developer"));
                    break;
                }
            }
        }
        
        return extractedSkills.stream().distinct().collect(Collectors.toList());
    }
    
    private double calculateImportance(String skill, String careerStage, String goals) {
        double importance = 0.0;
        
        // Career relevance
        List<String> careerSkills = getRequiredSkillsForCareer(careerStage);
        if (careerSkills.contains(skill)) {
            importance += 0.6;
        }
        
        // Goal relevance
        List<String> goalSkills = extractSkillsFromGoals(goals);
        if (goalSkills.contains(skill)) {
            importance += 0.4;
        }
        
        return importance;
    }
    
    public List<VideoResource> findYoutubeResourcesForSkill(String skill) {
        try {
            String query = skill + " tutorial";
            String url = String.format(
                "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=5&q=%s&type=video&key=%s",
                query.replace(" ", "+"),
                youtubeApiKey
            );
            
            YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
            
            if (response != null && response.getItems() != null) {
                return response.getItems().stream()
                    .map(this::convertToVideoResource)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // Fallback to default resources if API call fails
            return getDefaultResourcesForSkill(skill);
        }
        
        return Collections.emptyList();
    }
    
    private VideoResource convertToVideoResource(YouTubeSearchItem item) {
        VideoResource resource = new VideoResource();
        resource.setTitle(item.getSnippet().getTitle());
        resource.setDescription(item.getSnippet().getDescription());
        resource.setUrl("https://www.youtube.com/watch?v=" + item.getId().getVideoId());
        resource.setPlatform("YouTube");
        
        return resource;
    }
    
    private List<VideoResource> getDefaultResourcesForSkill(String skill) {
        // Return some default resources if YouTube API fails
        VideoResource resource = new VideoResource();
        resource.setTitle("Learn " + skill + " - Comprehensive Tutorial");
        resource.setDescription("A complete guide to learning " + skill + " from scratch");
        resource.setUrl("https://www.youtube.com/results?search_query=" + skill.replace(" ", "+") + "+tutorial");
        resource.setPlatform("YouTube");
        
        return Collections.singletonList(resource);
    }
    
    public LearningPathData generateLearningPath(UserProfile profile) {
        // Extract profile data
        List<String> userSkills = profile.getSkills();
        String careerStage = profile.getCareerStage();
        String goals = profile.getGoals();
        int hoursPerWeek = profile.getHoursPerWeek();
        
        // Find skill gaps
        List<SkillGap> skillGaps = findSkillGaps(userSkills, careerStage, goals);
        
        // Limit to top 5 skills to keep focus
        List<SkillGap> topSkills = skillGaps.stream()
                .limit(5)
                .collect(Collectors.toList());
        
        // Create 10-week plan
        List<WeekPlan> weeks = new ArrayList<>();
        int totalHours = hoursPerWeek * 10;
        
        // Calculate total importance for allocation
        double totalImportance = topSkills.stream()
                .mapToDouble(SkillGap::getImportance)
                .sum();
        
        // If no skills have importance, set equal importance
        if (totalImportance == 0) {
            for (SkillGap skill : topSkills) {
                skill.setImportance(1.0);
            }
            totalImportance = topSkills.size();
        }
        
        // Allocate hours and create weekly plans
        for (int i = 0; i < topSkills.size(); i++) {
            SkillGap skill = topSkills.get(i);
            
            // Calculate hours based on importance
            int skillHours = (int) Math.ceil((skill.getImportance() / totalImportance) * totalHours);
            
            // Find resources
            List<VideoResource> resources = findYoutubeResourcesForSkill(skill.getName());
            
            // Generate projects
            List<ProjectIdea> projects = generateProjectIdeas(skill.getName());
            
            // Create week plan
            WeekPlan weekPlan = new WeekPlan();
            weekPlan.setWeekNumber(i + 1);
            weekPlan.setSkill(skill.getName());
            weekPlan.setDescription(skill.getDescription());
            weekPlan.setHoursAllocated(skillHours);
            weekPlan.setResources(resources);
            weekPlan.setProjects(projects);
            
            weeks.add(weekPlan);
        }
        
        // Fill remaining weeks with review or advanced topics
        while (weeks.size() < 10) {
            WeekPlan reviewWeek = new WeekPlan();
            reviewWeek.setWeekNumber(weeks.size() + 1);
            reviewWeek.setReviewWeek(true);
            reviewWeek.setDescription("Review and practice week");
            reviewWeek.setHoursAllocated(hoursPerWeek);
            
            // Get all skills for review
            List<String> reviewSkills = topSkills.stream()
                    .map(SkillGap::getName)
                    .collect(Collectors.toList());
            reviewWeek.setReviewSkills(reviewSkills);
            
            // Add a consolidation project
            ProjectIdea project = new ProjectIdea();
            project.setTitle("Consolidation Project");
            project.setDescription("Create a project that combines multiple skills you've learned");
            reviewWeek.setProjects(Collections.singletonList(project));
            
            weeks.add(reviewWeek);
        }
        
        // Build the complete learning path data
        LearningPathData pathData = new LearningPathData();
        pathData.setSkillGaps(topSkills);
        pathData.setWeeks(weeks);
        pathData.setTotalLearningHours(totalHours);
        
        return pathData;
    }
    
    private List<ProjectIdea> generateProjectIdeas(String skill) {
        List<ProjectIdea> ideas = new ArrayList<>();
        
        // Base project ideas on skill
        switch (skill.toLowerCase()) {
            case "java":
                ideas.add(createProject("Task Manager API", "Create a RESTful API for managing tasks with Java"));
                ideas.add(createProject("E-commerce System", "Build a simple e-commerce backend with Java"));
                break;
                
            case "spring":
            case "spring boot":
                ideas.add(createProject("Blog API", "Create a RESTful API for a blog using Spring Boot"));
                ideas.add(createProject("User Authentication", "Implement user auth with Spring Security"));
                break;
                
            case "react":
                ideas.add(createProject("Todo App", "Build a todo list application with React"));
                ideas.add(createProject("Weather Dashboard", "Create a weather app using React and a weather API"));
                break;
                
            case "python":
                ideas.add(createProject("Data Analysis", "Analyze a dataset and visualize the results"));
                ideas.add(createProject("Web Scraper", "Build a web scraper to collect and analyze data"));
                break;
                
            default:
                // Generic project ideas
                ideas.add(createProject(skill + " Practice Project", 
                        "Build a small project to practice " + skill));
                ideas.add(createProject(skill + " Integration", 
                        "Integrate " + skill + " into an existing project"));
        }
        
        return ideas;
    }
    
    private ProjectIdea createProject(String title, String description) {
        ProjectIdea idea = new ProjectIdea();
        idea.setTitle(title);
        idea.setDescription(description);
        return idea;
    }
    
    // Inner classes for data structures
    
    public static class SkillDatabase {
        private List<SkillInfo> skills;
        private List<CareerPath> careerPaths;
        
        public List<SkillInfo> getSkills() {
            return skills;
        }
        
        public void setSkills(List<SkillInfo> skills) {
            this.skills = skills;
        }
        
        public List<CareerPath> getCareerPaths() {
            return careerPaths;
        }
        
        public void setCareerPaths(List<CareerPath> careerPaths) {
            this.careerPaths = careerPaths;
        }
    }
    
    public static class SkillInfo {
        private String name;
        private String description;
        private List<String> tags;
        private int difficulty;
        private List<String> relatedSkills;
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public List<String> getTags() {
            return tags;
        }
        
        public void setTags(List<String> tags) {
            this.tags = tags;
        }
        
        public int getDifficulty() {
            return difficulty;
        }
        
        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }
        
        public List<String> getRelatedSkills() {
            return relatedSkills;
        }
        
        public void setRelatedSkills(List<String> relatedSkills) {
            this.relatedSkills = relatedSkills;
        }
    }
    
    public static class CareerPath {
        private String name;
        private List<String> skills;
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<String> getSkills() {
            return skills;
        }
        
        public void setSkills(List<String> skills) {
            this.skills = skills;
        }
    }
    
    public static class SkillGap {
        private String name;
        private String description;
        private double importance;
        private int difficulty;
        
        // Getters and setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public double getImportance() {
            return importance;
        }
        
        public void setImportance(double importance) {
            this.importance = importance;
        }
        
        public int getDifficulty() {
            return difficulty;
        }
        
        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }
    }
    
    public static class VideoResource {
        private String title;
        private String description;
        private String url;
        private String platform;
        
        // Getters and setters
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getPlatform() {
            return platform;
        }
        
        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
    
    public static class ProjectIdea {
        private String title;
        private String description;
        
        // Getters and setters
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    public static class WeekPlan {
        private int weekNumber;
        private String skill;
        private String description;
        private int hoursAllocated;
        private List<VideoResource> resources;
        private List<ProjectIdea> projects;
        private boolean isReviewWeek;
        private List<String> reviewSkills;
        
        // Getters and setters
        public int getWeekNumber() {
            return weekNumber;
        }
        
        public void setWeekNumber(int weekNumber) {
            this.weekNumber = weekNumber;
        }
        
        public String getSkill() {
            return skill;
        }
        
        public void setSkill(String skill) {
            this.skill = skill;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public int getHoursAllocated() {
            return hoursAllocated;
        }
        
        public void setHoursAllocated(int hoursAllocated) {
            this.hoursAllocated = hoursAllocated;
        }
        
        public List<VideoResource> getResources() {
            return resources;
        }
        
        public void setResources(List<VideoResource> resources) {
            this.resources = resources;
        }
        
        public List<ProjectIdea> getProjects() {
            return projects;
        }
        
        public void setProjects(List<ProjectIdea> projects) {
            this.projects = projects;
        }
        
        public boolean isReviewWeek() {
            return isReviewWeek;
        }
        
        public void setReviewWeek(boolean reviewWeek) {
            isReviewWeek = reviewWeek;
        }
        
        public List<String> getReviewSkills() {
            return reviewSkills;
        }
        
        public void setReviewSkills(List<String> reviewSkills) {
            this.reviewSkills = reviewSkills;
        }
    }
    
    public static class LearningPathData {
        private List<SkillGap> skillGaps;
        private List<WeekPlan> weeks;
        private int totalLearningHours;
        
        // Getters and setters
        public List<SkillGap> getSkillGaps() {
            return skillGaps;
        }
        
        public void setSkillGaps(List<SkillGap> skillGaps) {
            this.skillGaps = skillGaps;
        }
        
        public List<WeekPlan> getWeeks() {
            return weeks;
        }
        
        public void setWeeks(List<WeekPlan> weeks) {
            this.weeks = weeks;
        }
        
        public int getTotalLearningHours() {
            return totalLearningHours;
        }
        
        public void setTotalLearningHours(int totalLearningHours) {
            this.totalLearningHours = totalLearningHours;
        }
    }
    
    // YouTube API response classes
    
    public static class YouTubeSearchResponse {
        private List<YouTubeSearchItem> items;
        
        public List<YouTubeSearchItem> getItems() {
            return items;
        }
        
        public void setItems(List<YouTubeSearchItem> items) {
            this.items = items;
        }
    }
    
    public static class YouTubeSearchItem {
        private YouTubeId id;
        private YouTubeSnippet snippet;
        
        public YouTubeId getId() {
            return id;
        }
        
        public void setId(YouTubeId id) {
            this.id = id;
        }
        
        public YouTubeSnippet getSnippet() {
            return snippet;
        }
        
        public void setSnippet(YouTubeSnippet snippet) {
            this.snippet = snippet;
        }
    }
    
    public static class YouTubeId {
        private String videoId;
        
        public String getVideoId() {
            return videoId;
        }
        
        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }
    
    public static class YouTubeSnippet {
        private String title;
        private String description;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}