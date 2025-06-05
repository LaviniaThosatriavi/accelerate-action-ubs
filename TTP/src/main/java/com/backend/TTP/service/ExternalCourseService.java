package com.backend.TTP.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExternalCourseService {
    
    private final Map<String, String> courseCategoryUrls = new HashMap<>();
    private final Map<String, String> freeCodeCampPlaylists = new HashMap<>();
    
    public ExternalCourseService() {
        // Initialize Coursera category URLs
        initializeCourseCategoryUrls();
        // Initialize FreeCodeCamp playlists
        initializeFreeCodeCampPlaylists();
    }
    
    private void initializeCourseCategoryUrls() {
        // Core CS and Data Science categories
        courseCategoryUrls.put("COMPUTER_SCIENCE", 
            "https://www.coursera.org/search?topic=Computer%20Science&sortBy=BEST_MATCH");
        courseCategoryUrls.put("DATA_SCIENCE", 
            "https://www.coursera.org/search?topic=Data%20Science&sortBy=BEST_MATCH");
        courseCategoryUrls.put("INFORMATION_TECHNOLOGY", 
            "https://www.coursera.org/search?topic=Information%20Technology&sortBy=BEST_MATCH");
        
        // Blockchain and Web3 specific categories
        courseCategoryUrls.put("BLOCKCHAIN", 
            "https://www.coursera.org/search?query=blockchain&sortBy=BEST_MATCH");
        courseCategoryUrls.put("ETHEREUM", 
            "https://www.coursera.org/search?query=ethereum&sortBy=BEST_MATCH");
        courseCategoryUrls.put("SOLIDITY", 
            "https://www.coursera.org/search?query=solidity&sortBy=BEST_MATCH");
        courseCategoryUrls.put("SMART_CONTRACTS", 
            "https://www.coursera.org/search?query=smart%20contracts&sortBy=BEST_MATCH");
        courseCategoryUrls.put("WEB3", 
            "https://www.coursera.org/search?query=web3&sortBy=BEST_MATCH");
        courseCategoryUrls.put("DAPP", 
            "https://www.coursera.org/search?query=decentralized%20application&sortBy=BEST_MATCH");
        courseCategoryUrls.put("IPFS", 
            "https://www.coursera.org/search?query=ipfs&sortBy=BEST_MATCH");
        
        // AI and ML
        courseCategoryUrls.put("ARTIFICIAL_INTELLIGENCE", 
            "https://www.coursera.org/search?query=artificial%20intelligence&sortBy=BEST_MATCH");
        courseCategoryUrls.put("MACHINE_LEARNING", 
            "https://www.coursera.org/search?query=machine%20learning&sortBy=BEST_MATCH");
        
        // Web and Mobile Development
        courseCategoryUrls.put("WEB_DEVELOPMENT", 
            "https://www.coursera.org/search?query=web%20development&sortBy=BEST_MATCH");
        courseCategoryUrls.put("MOBILE_DEVELOPMENT", 
            "https://www.coursera.org/search?query=mobile%20development&sortBy=BEST_MATCH");
        courseCategoryUrls.put("CSS", 
            "https://www.coursera.org/search?query=css&sortBy=BEST_MATCH");
        courseCategoryUrls.put("RESPONSIVE_DESIGN", 
            "https://www.coursera.org/search?query=responsive%20design&sortBy=BEST_MATCH");
        courseCategoryUrls.put("UI_UX", 
            "https://www.coursera.org/search?query=ui%20ux%20design&sortBy=BEST_MATCH");
        
        // DevOps and Cloud
        courseCategoryUrls.put("CYBERSECURITY", 
            "https://www.coursera.org/search?query=cybersecurity&sortBy=BEST_MATCH");
        courseCategoryUrls.put("CLOUD_COMPUTING", 
            "https://www.coursera.org/search?query=cloud%20computing&sortBy=BEST_MATCH");
        courseCategoryUrls.put("DEVOPS", 
            "https://www.coursera.org/search?query=devops&sortBy=BEST_MATCH");
        
        // Project Management and Software Design
        courseCategoryUrls.put("PROJECT_MANAGEMENT", 
            "https://www.coursera.org/search?query=project%20management&sortBy=BEST_MATCH");
        courseCategoryUrls.put("SOFTWARE_DESIGN", 
            "https://www.coursera.org/search?query=software%20design&sortBy=BEST_MATCH");
            
        // Programming Languages
        courseCategoryUrls.put("PYTHON", 
            "https://www.coursera.org/search?query=python&sortBy=BEST_MATCH");
        courseCategoryUrls.put("JAVASCRIPT", 
            "https://www.coursera.org/search?query=javascript&sortBy=BEST_MATCH");
        courseCategoryUrls.put("JAVA", 
            "https://www.coursera.org/search?query=java&sortBy=BEST_MATCH");
        courseCategoryUrls.put("CPP", 
            "https://www.coursera.org/search?query=c%2B%2B&sortBy=BEST_MATCH");
        courseCategoryUrls.put("CSHARP", 
            "https://www.coursera.org/search?query=c%23&sortBy=BEST_MATCH");
        courseCategoryUrls.put("C_PROGRAMMING", 
            "https://www.coursera.org/search?query=c%20programming&sortBy=BEST_MATCH");
        courseCategoryUrls.put("R_PROGRAMMING", 
            "https://www.coursera.org/search?query=r%20programming&sortBy=BEST_MATCH");
        courseCategoryUrls.put("GOLANG", 
            "https://www.coursera.org/search?query=golang&sortBy=BEST_MATCH");
        
        // Version Control
        courseCategoryUrls.put("GIT", 
            "https://www.coursera.org/search?query=git&sortBy=BEST_MATCH");
        
        // Default for any other category
        courseCategoryUrls.put("DEFAULT", 
            "https://www.coursera.org/search?topic=Computer%20Science&topic=Data%20Science&topic=Information%20Technology&sortBy=BEST_MATCH");
    }
    
    private void initializeFreeCodeCampPlaylists() {
        // Core CS and Data Science playlists
        freeCodeCampPlaylists.put("COMPUTER_SCIENCE", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbn5emQ3RRG8gEBqkhf_5vxD");
        freeCodeCampPlaylists.put("DATA_SCIENCE", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbmtRpBX6KFaGkFgCvTkd5Xx");
        
        // Blockchain and Web3 specific playlists
        freeCodeCampPlaylists.put("BLOCKCHAIN", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        freeCodeCampPlaylists.put("ETHEREUM", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        freeCodeCampPlaylists.put("SOLIDITY", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbncUQCV5GdwScZo8db2hfYh");
        freeCodeCampPlaylists.put("SMART_CONTRACTS", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbncUQCV5GdwScZo8db2hfYh");
        freeCodeCampPlaylists.put("WEB3", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        freeCodeCampPlaylists.put("DAPP", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        freeCodeCampPlaylists.put("IPFS", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        
        // Programming languages
        freeCodeCampPlaylists.put("PYTHON", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbnqBxcdjVGgT3uVR10bzTEB");
        freeCodeCampPlaylists.put("JAVASCRIPT", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbleDe3_ZA8h3AO2rXar-q2V");
        
        // Web Development
        freeCodeCampPlaylists.put("WEB_DEVELOPMENT", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAblfum5WiQblKPwIbqYXkDoC");
        freeCodeCampPlaylists.put("CSS", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAblfum5WiQblKPwIbqYXkDoC");
        freeCodeCampPlaylists.put("RESPONSIVE_DESIGN", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAblfum5WiQblKPwIbqYXkDoC");
        freeCodeCampPlaylists.put("UI_UX", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAblfum5WiQblKPwIbqYXkDoC");
        
        // Version Control
        freeCodeCampPlaylists.put("GIT", 
            "https://www.youtube.com/playlist?list=PLWKjhJtqVAbn_zg43Yi3qz1zucq8RNPJ5");
        
        // Default for any other category
        freeCodeCampPlaylists.put("DEFAULT", 
            "https://www.youtube.com/c/Freecodecamp/playlists");
    }
    
    /**
     * Get the Coursera URL for a specific learning category
     */
    public String getCourseraUrlForCategory(String category) {
        // NULL SAFETY: Handle null category
        if (category == null) {
            return courseCategoryUrls.get("DEFAULT");
        }
        
        if (courseCategoryUrls.containsKey(category)) {
            return courseCategoryUrls.get(category);
        } else {
            return courseCategoryUrls.get("DEFAULT");
        }
    }
    
    /**
     * Get FreeCodeCamp YouTube playlist URL for a specific learning category
     */
    public String getFreeCodeCampPlaylistForCategory(String category) {
        // NULL SAFETY: Handle null category
        if (category == null) {
            return freeCodeCampPlaylists.get("DEFAULT");
        }
        
        if (freeCodeCampPlaylists.containsKey(category)) {
            return freeCodeCampPlaylists.get(category);
        } else {
            return freeCodeCampPlaylists.get("DEFAULT");
        }
    }
    
    /**
     * Get individual FreeCodeCamp videos for a specific learning category
     * @return Array of video URLs in the playlist
     */
    public String[] getFreeCodeCampVideosForCategory(String category) {
        // NULL SAFETY: Handle null category
        if (category == null) {
            category = "DEFAULT";
        }
        
        // Define videos for different categories
        switch(category) {
            case "BLOCKCHAIN":
            case "ETHEREUM":
            case "WEB3":
            case "DAPP":
                return new String[] {
                    "https://www.youtube.com/watch?v=gyMwXuJrbJQ", // 32-Hour Blockchain Course
                    "https://www.youtube.com/watch?v=M576WGiDBdQ", // Blockchain Tutorial
                    "https://www.youtube.com/watch?v=xWFba_9QYmc"  // Solidity Tutorial
                };
                
            case "SOLIDITY":
            case "SMART_CONTRACTS":
                return new String[] {
                    "https://www.youtube.com/watch?v=ipwxYa-F1uY", // Solidity, Blockchain, Smart Contract Course
                    "https://www.youtube.com/watch?v=M576WGiDBdQ", // Ethereum Smart Contract
                    "https://www.youtube.com/watch?v=xWFba_9QYmc"  // Solidity Tutorial
                };
                
            case "IPFS":
                return new String[] {
                    "https://www.youtube.com/watch?v=5Uj6uR3fp-U", // IPFS Tutorial
                    "https://www.youtube.com/watch?v=Obnxs_GC9Bk", // Decentralized Storage
                    "https://www.youtube.com/watch?v=gyMwXuJrbJQ"  // Blockchain Course (includes IPFS)
                };
                
            case "JAVASCRIPT":
            case "web3.js":
                return new String[] {
                    "https://www.youtube.com/watch?v=PkZNo7MFNFg", // JavaScript Tutorial
                    "https://www.youtube.com/watch?v=jS4aFq5-91M", // JavaScript Full Course
                    "https://www.youtube.com/watch?v=a0osIaAOFSE"  // Web3.js Tutorial
                };
                
            case "CSS":
            case "RESPONSIVE_DESIGN":
                return new String[] {
                    "https://www.youtube.com/watch?v=OXGznpKZ_sA", // CSS Full Course
                    "https://www.youtube.com/watch?v=1Rs2ND1ryYc", // CSS Tutorial
                    "https://www.youtube.com/watch?v=3T4BsrBISnI"  // Responsive Web Design
                };
                
            case "GIT":
                return new String[] {
                    "https://www.youtube.com/watch?v=RGOj5yH7evk", // Git and GitHub for Beginners
                    "https://www.youtube.com/watch?v=Uszj_k0DGsg", // Git Tutorial
                    "https://www.youtube.com/watch?v=8JJ101D3knE"  // Advanced Git
                };
                
            case "SOFTWARE_DESIGN":
                return new String[] {
                    "https://www.youtube.com/watch?v=msXL2oDexqw", // Software Architecture
                    "https://www.youtube.com/watch?v=SiBw7os-_zI", // Design Patterns
                    "https://www.youtube.com/watch?v=m2nj5sUE3hg"  // SOLID Principles
                };
                
            case "UI_UX":
                return new String[] {
                    "https://www.youtube.com/watch?v=c9Wg6Cb_YlU", // UI/UX Design Tutorial
                    "https://www.youtube.com/watch?v=3aOU9MbIJ1g", // UX Design Course
                    "https://www.youtube.com/watch?v=TgqeRTwZvIo"  // Figma Tutorial
                };
                
            default:
                return new String[] {
                    "https://www.youtube.com/watch?v=zOjov-2OZ0E", // Programming Concepts
                    "https://www.youtube.com/watch?v=XGf2GcyHPhc", // Learn to Code
                    "https://www.youtube.com/watch?v=PkZNo7MFNFg"  // JavaScript for Beginners
                };
        }
    }
}