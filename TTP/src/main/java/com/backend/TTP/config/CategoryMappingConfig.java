package com.backend.TTP.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CategoryMappingConfig {
    
    @Bean
    public Map<String, List<String>> categoryKeywordMap() {
        Map<String, List<String>> map = new HashMap<>();
        
        // Blockchain related
        map.put("BLOCKCHAIN", List.of("BLOCKCHAIN", "CRYPTO", "BITCOIN", "ETHEREUM", "WEB3", "DECENTRALIZED", "DLT"));
        map.put("SOLIDITY", List.of("SOLIDITY", "SMART CONTRACT", "ETHEREUM"));
        map.put("SMART_CONTRACTS", List.of("SMART CONTRACT", "SOLIDITY", "WEB3", "DAPP"));
        map.put("WEB3", List.of("WEB3", "WEB3.JS", "DECENTRALIZED", "DAPP"));
        map.put("IPFS", List.of("IPFS", "DECENTRALIZED STORAGE", "FILECOIN"));
        map.put("ETHEREUM", List.of("ETHEREUM", "ETH", "GETH", "EVM"));
        map.put("DAPP", List.of("DAPP", "DECENTRALIZED APP", "WEB3"));
        
        // Web Development
        map.put("JAVASCRIPT", List.of("JAVASCRIPT", "JS", "REACT", "NODE", "ANGULAR", "VUE"));
        map.put("WEB_DEVELOPMENT", List.of("WEB", "FRONTEND", "FRONT-END", "HTML", "CSS", "JAVASCRIPT"));
        map.put("CSS", List.of("CSS", "STYLESHEET", "RESPONSIVE DESIGN"));
        map.put("RESPONSIVE_DESIGN", List.of("RESPONSIVE", "MOBILE-FRIENDLY", "ADAPTIVE DESIGN"));
        
        // UI/UX
        map.put("UI_UX", List.of("UI/UX", "USER INTERFACE", "USER EXPERIENCE", "DESIGN PRINCIPLES"));
        
        // Version Control
        map.put("GIT", List.of("GIT", "VERSION CONTROL", "GITHUB", "GITLAB"));
        
        // Software Design
        map.put("SOFTWARE_DESIGN", List.of("SOFTWARE DESIGN", "ARCHITECTURE", "DESIGN PATTERNS", "SOLID"));
        
        // Core programming
        map.put("PYTHON", List.of("PYTHON", "PANDAS", "NUMPY"));
        map.put("JAVA", List.of("JAVA ", "SPRING", "JAVA.", "JAVA,"));
        map.put("CPP", List.of("C++", "CPP"));
        map.put("CSHARP", List.of("C#", "CSHARP", ".NET"));
        map.put("C_PROGRAMMING", List.of(" C ", "C PROGRAMMING"));
        map.put("R_PROGRAMMING", List.of(" R ", "R PROGRAMMING", "RSTUDIO"));
        map.put("GOLANG", List.of("GO ", "GOLANG"));
        
        // Data Science and AI
        map.put("DATA_SCIENCE", List.of("DATA", "ANALYTICS", "DATA SCIENCE"));
        map.put("ARTIFICIAL_INTELLIGENCE", List.of("AI", "ARTIFICIAL INTELLIGENCE"));
        map.put("MACHINE_LEARNING", List.of("MACHINE LEARNING", "ML"));
        
        // DevOps and Cloud
        map.put("CLOUD_COMPUTING", List.of("CLOUD", "AWS", "AZURE", "GCP"));
        map.put("DEVOPS", List.of("DEVOPS", "CI/CD", "CONTINUOUS INTEGRATION"));
        map.put("CYBERSECURITY", List.of("SECURITY", "CYBER", "ENCRYPTION"));
        
        // Mobile Development
        map.put("MOBILE_DEVELOPMENT", List.of("MOBILE", "APP", "IOS", "ANDROID"));
        
        // Project Management
        map.put("PROJECT_MANAGEMENT", List.of("PROJECT", "MANAGEMENT", "PM", "AGILE", "SCRUM"));
        
        return map;
    }
}